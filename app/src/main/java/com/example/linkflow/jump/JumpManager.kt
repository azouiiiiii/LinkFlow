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

        return when (schedule.appType) {
            AppType.BROWSER ->
                BrowserJumpHandler().handle(context, schedule)

            else -> {
                if (schedule.extraData.isNotBlank()) {
                    DeepLinkJumpHandler().handle(context, schedule)
                        ?: AppJumpHandler().handle(context, schedule)
                } else {
                    AppJumpHandler().handle(context, schedule)
                }
            }
        }
    }
}