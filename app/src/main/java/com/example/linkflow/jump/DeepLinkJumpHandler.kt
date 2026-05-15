// app内功能跳转（如腾讯会议的某具体会议、微信支付等）

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

        val url = when (schedule.appType) {

            AppType.BROWSER -> {
                if (schedule.extraData.isBlank())
                    "https://www.google.com"
                else
                    schedule.extraData
            }

            else -> return null
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}