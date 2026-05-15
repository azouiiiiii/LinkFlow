package com.example.linkflow.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkflow.reminder.Scheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.linkflow.AppType

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    application: Application
) : AndroidViewModel(application) {

    // 当前选中的日期
    private val _selectedDate = MutableStateFlow("")

    // 根据日期动态过滤
    @OptIn(ExperimentalCoroutinesApi::class)
    val schedulesForSelectedDate: Flow<List<Schedule>> =
        _selectedDate.flatMapLatest { date ->

            if (date.isEmpty()) {

                repository.allSchedules

            } else {

                repository.allSchedules.map { list ->
                    list.filter { it.date == date }
                }
            }
        }

    // 更新当前日期
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // 添加日程
    fun addSchedule(
        content: String,
        triggerTime: Long,
        date: String,
        appType: AppType,
        extraData: String,
        reminderType: ReminderType
    ) {

        viewModelScope.launch {

            val safeTriggerTime =
                if (triggerTime <= System.currentTimeMillis()) {

                    System.currentTimeMillis() + 5000

                } else {

                    triggerTime
                }

            val schedule = Schedule(

                date = date,

                content = content,

                triggerTime = safeTriggerTime,

                appType = appType,

                extraData = extraData,

                reminderType = reminderType
            )

            val id = repository.insert(schedule)

            val savedSchedule =
                schedule.copy(id = id.toInt())

            val appContext =
                getApplication<Application>()

            Scheduler.scheduleReminder(
                appContext,
                savedSchedule
            )
        }
    }

    // 删除日程
    fun deleteSchedule(schedule: Schedule) {

        viewModelScope.launch {

            repository.delete(schedule)
        }
    }
}