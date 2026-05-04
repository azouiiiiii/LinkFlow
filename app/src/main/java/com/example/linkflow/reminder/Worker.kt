package com.example.linkflow.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.linkflow.data.AppDatabase

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        android.util.Log.d("Worker", "Worker triggered")

        val scheduleId = inputData.getInt("scheduleId", -1)

        val dao = AppDatabase.getDatabase(applicationContext).scheduleDao()
        val schedule = dao.getScheduleById(scheduleId)

        if (schedule == null) return Result.failure()

        NotificationHelper.showNotification(
            applicationContext,
            schedule.id,
            "日程提醒",
            schedule.content,
            schedule.jumpUrl   // ⭐ 核心
        )

        return Result.success()
    }
}