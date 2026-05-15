// 跳转总控（应用哪种跳转）

package com.example.linkflow.jump

import android.content.Context
import android.content.Intent

import com.example.linkflow.AppType
import com.example.linkflow.schedule.Schedule

object JumpManager {

    fun jump(
        context: Context,
        schedule: Schedule
    ): Intent? {

        val handler: JumpHandler = when (schedule.appType) {

            AppType.BROWSER ->
                BrowserJumpHandler()

            else ->
                AppJumpHandler()
        }

        return handler.handle(context, schedule)
    }
}