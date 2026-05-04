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

    val allSchedules: Flow<List<Schedule>> = repository.allSchedules

    fun addSchedule(content: String, triggerTime: Long, url: String) {

        viewModelScope.launch {

            val safeTriggerTime = if (triggerTime <= System.currentTimeMillis()) {
                System.currentTimeMillis() + 5000
            } else {
                triggerTime
            }

            val schedule = Schedule(
                content = content,
                triggerTime = safeTriggerTime,
                jumpUrl = url   // ⭐ 用户输入
            )

            val id = repository.insert(schedule)
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