package com.example.linkflow.reminder

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.linkflow.data.AppDatabase
import com.example.linkflow.jump.JumpManager
import com.example.linkflow.schedule.ReminderType

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val scheduleId = inputData.getInt("scheduleId", -1)

        val dao = AppDatabase.getDatabase(applicationContext).scheduleDao()

        val schedule = dao.getScheduleById(scheduleId)
            ?: return Result.failure()

        val reminderTypeStr = inputData.getString("reminderType") ?: "STATIC"
        val reminderType = ReminderType.valueOf(reminderTypeStr)

        val intent = if (reminderType == ReminderType.DYNAMIC) {
            JumpManager.jump(applicationContext, schedule)
        } else {
            null
        }

        NotificationHelper.showNotification(
            context = applicationContext,
            title = "日程提醒",
            content = schedule.content,
            intent = intent
        )

        return Result.success()
    }
}