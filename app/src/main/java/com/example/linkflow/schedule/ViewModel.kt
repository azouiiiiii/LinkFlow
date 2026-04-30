// 具体逻辑实现（什么情况调用什么动作）

package com.example.linkflow.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.linkflow.reminder.Scheduler

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    application: Application
) : AndroidViewModel(application) {

    fun addSchedule(content: String, triggerTime: Long) {

        viewModelScope.launch {

            val schedule = Schedule(
                content = content,
                triggerTime = triggerTime,
                jumpUrl = "https://www.google.com"
            )

            // ✔ 存数据库
            val id = repository.insert(schedule)

            // ✔ 补 id
            val savedSchedule = schedule.copy(id = id.toInt())

            // ✔ context
            val appContext = getApplication<Application>()

            // ✔ 调度
            Scheduler.scheduleReminder(appContext, savedSchedule)
        }
    }
}