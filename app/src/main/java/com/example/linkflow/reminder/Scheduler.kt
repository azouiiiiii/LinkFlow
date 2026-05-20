package com.example.linkflow.reminder

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

import com.example.linkflow.schedule.Schedule


object Scheduler {

    fun scheduleReminder(
        context: Context,
        schedule: Schedule
    ) {
        enqueueWork(context, schedule)
    }

    fun cancelReminder(context: Context, scheduleId: Int) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("schedule_$scheduleId")
    }

    private fun enqueueWork(
        context: Context,
        schedule: Schedule
    ) {

        val delay = schedule.triggerTime - System.currentTimeMillis()

        android.util.Log.d("Scheduler", "delay = $delay")

        if (delay <= 0) return

        val inputData = workDataOf(
            "scheduleId" to schedule.id,
            "reminderType" to schedule.reminderType.name
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("schedule_${schedule.id}")
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }
}
