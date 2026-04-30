// 计时器（决定动作触发时机）

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

        val delay = schedule.triggerTime - System.currentTimeMillis()

        if (delay <= 0) return

        val inputData = workDataOf(
            "scheduleId" to schedule.id
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }
}