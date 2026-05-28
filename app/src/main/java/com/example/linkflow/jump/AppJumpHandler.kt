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

        val packageName = when (schedule.appType) {

            AppType.WECHAT ->
                AppRegistry.WECHAT

            AppType.BILIBILI ->
                AppRegistry.BILIBILI

            AppType.ZHIHU ->
                AppRegistry.ZHIHU

            AppType.ALIPAY ->
                AppRegistry.ALIPAY

            AppType.TENCENT_MEETING ->
                AppRegistry.TENCENT_MEETING

            else -> return null
        }

        return context.packageManager
            .getLaunchIntentForPackage(packageName)
    }
}