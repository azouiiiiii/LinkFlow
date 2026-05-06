package com.example.linkflow.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkflow.reminder.Scheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    application: Application
) : AndroidViewModel(application) {

    // 1. 用于存储当前选中的日期，默认为空（或者你可以设置为今天的日期字符串）
    private val _selectedDate = MutableStateFlow("")

    // 2. 暴露给 UI 的数据流：根据 _selectedDate 的变化自动过滤数据
    // 使用 flatMapLatest，当日期改变时，会自动切换到新的查询流
    @OptIn(ExperimentalCoroutinesApi::class)
    val schedulesForSelectedDate: Flow<List<Schedule>> = _selectedDate.flatMapLatest { date ->
        if (date.isEmpty()) {
            repository.allSchedules // 如果没选日期，显示全部（可选）
        } else {
            // 这里建议在 Repository 中实现根据日期查询的方法
            // 如果暂时没有，可以先获取全部然后在内存中过滤
            repository.allSchedules.map { list ->
                list.filter { it.date == date }
            }
        }
    }

    // 3. 供 Activity 调用的方法：更新当前选中的日期
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // 保持 addSchedule 逻辑不变，因为它已经支持传入 date 参数了
    fun addSchedule(content: String, triggerTime: Long, url: String, date: String) {
        viewModelScope.launch {
            val safeTriggerTime = if (triggerTime <= System.currentTimeMillis()) {
                System.currentTimeMillis() + 5000
            } else {
                triggerTime
            }

            val schedule = Schedule(
                date = date,
                content = content,
                triggerTime = safeTriggerTime,
                jumpUrl = url
            )

            // 插入数据库并获取生成的 ID
            val id = repository.insert(schedule)

            // 如果 Schedule 的 id 不是主键而是普通字段，这里 copy 是为了同步数据库生成的 ID
            val savedSchedule = schedule.copy(id = id.toInt())

            val appContext = getApplication<Application>()
            Scheduler.scheduleReminder(appContext, savedSchedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.delete(schedule)
        }
    }
}