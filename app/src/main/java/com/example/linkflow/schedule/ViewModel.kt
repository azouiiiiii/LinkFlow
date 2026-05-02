package com.example.linkflow.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.example.linkflow.reminder.Scheduler

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    application: Application
) : AndroidViewModel(application) {

    // 🔥 给 UI 用的数据流
    val allSchedules: Flow<List<Schedule>> = repository.allSchedules

    fun addSchedule(content: String, triggerTime: Long) {

        android.util.Log.d("ScheduleVM", "addSchedule called")

        viewModelScope.launch {

            val safeTriggerTime = if (triggerTime <= System.currentTimeMillis()) {
                System.currentTimeMillis() + 5000
            } else {
                triggerTime
            }

            val schedule = Schedule(
                content = content,
                triggerTime = safeTriggerTime,
                jumpUrl = "https://www.google.com"
            )

            // ✔ 存数据库
            val id = repository.insert(schedule)

            // ✔ 补 id
            val savedSchedule = schedule.copy(id = id.toInt())

            // ✔ 调度提醒
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