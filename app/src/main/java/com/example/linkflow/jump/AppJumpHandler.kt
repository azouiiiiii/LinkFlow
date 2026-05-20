package com.example.linkflow.jump

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.linkflow.schedule.Schedule
import com.example.linkflow.AppType

class AppJumpHandler : JumpHandler {

    override fun handle(
        context: Context,
        schedule: Schedule
    ): Intent? {

        val uri = when (schedule.appType) {
            AppType.WECHAT -> DeepLinkRoutes.wechatHome()
            AppType.BILIBILI -> DeepLinkRoutes.bilibiliHome()
            AppType.ZHIHU -> DeepLinkRoutes.zhihuHome()
            AppType.ALIPAY -> DeepLinkRoutes.alipayHome()
            AppType.TENCENT_MEETING -> DeepLinkRoutes.tencentMeetingHome()
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
