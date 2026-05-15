// 浏览器跳转

package com.example.linkflow.jump

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.linkflow.schedule.Schedule

class BrowserJumpHandler : JumpHandler {

    override fun handle(
        context: Context,
        schedule: Schedule
    ): Intent? {

        val url =
            if (schedule.extraData.isBlank())
                "https://www.google.com"
            else
                schedule.extraData

        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}