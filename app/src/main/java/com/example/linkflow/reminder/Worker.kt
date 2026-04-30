// 动作调用（静态提醒/动态提醒）

package com.example.linkflow.reminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

import com.example.linkflow.data.AppDatabase

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val scheduleId = inputData.getInt("scheduleId", -1)

        val dao = AppDatabase.getDatabase(applicationContext).scheduleDao()
        val schedule = dao.getScheduleById(scheduleId)

        if (schedule == null) return Result.failure()

        NotificationHelper.showNotification(
            applicationContext,
            schedule.id,
            "日程提醒",
            schedule.content
        )

        return Result.success()
    }
}