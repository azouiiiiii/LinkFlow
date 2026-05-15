// 跳转app（包含检测是否安装对应app）

package com.example.linkflow.jump

import android.content.Context
import android.widget.Toast
import android.content.Intent

import com.example.linkflow.schedule.Schedule
import com.example.linkflow.AppType

class AppJumpHandler : JumpHandler {

    override fun handle(
        context: Context,
        schedule: Schedule
    ): Intent? {

        val target = when (schedule.appType) {

            AppType.WECHAT -> "com.tencent.mm"
            AppType.BILIBILI -> "tv.danmaku.bili"
            AppType.ZHIHU -> "com.zhihu.android"
            AppType.ALIPAY -> "com.eg.android.AlipayGphone"
            AppType.TENCENT_MEETING -> "com.tencent.wemeet.app"
            else -> null
        } ?: return null

        return context.packageManager
            .getLaunchIntentForPackage(target)
    }
}