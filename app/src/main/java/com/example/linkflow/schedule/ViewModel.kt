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

    private val _selectedDate = MutableStateFlow("")

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

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

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
            val savedSchedule = schedule.copy(id = id.toInt())
            val appContext = getApplication<Application>()

            Scheduler.scheduleReminder(appContext, savedSchedule)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val safeSchedule = if (schedule.triggerTime <= System.currentTimeMillis()) {
                schedule.copy(triggerTime = System.currentTimeMillis() + 5000)
            } else {
                schedule
            }
            repository.update(safeSchedule)
            val appContext = getApplication<Application>()

            Scheduler.cancelReminder(appContext, safeSchedule.id)
            Scheduler.scheduleReminder(appContext, safeSchedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.delete(schedule)
            Scheduler.cancelReminder(getApplication(), schedule.id)
        }
    }
}
