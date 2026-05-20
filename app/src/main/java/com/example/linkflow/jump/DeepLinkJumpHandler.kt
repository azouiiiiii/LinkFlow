package com.example.linkflow.jump

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.linkflow.schedule.Schedule
import com.example.linkflow.AppType

class DeepLinkJumpHandler : JumpHandler {

    override fun handle(
        context: Context,
        schedule: Schedule
    ): Intent? {

        if (schedule.extraData.isBlank()) return null

        val uri = when (schedule.appType) {
            AppType.BILIBILI -> DeepLinkRoutes.bilibiliVideo(schedule.extraData)
            AppType.ZHIHU -> DeepLinkRoutes.zhihuQuestion(schedule.extraData)
            AppType.TENCENT_MEETING -> DeepLinkRoutes.tencentMeeting(schedule.extraData)
            else -> return null
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

        return if (intent.resolveActivity(context.packageManager) != null) {
            intent
        } else {
            null
        }
    }
}
