// 跳转总控（应用哪种跳转）

package com.example.linkflow.jump

import android.content.Context

object JumpManager {

    // 当前只支持浏览器
    private val browserHandler = BrowserJumpHandler()

    fun jump(context: Context, url: String?) {

        if (url.isNullOrEmpty()) return

        when {
            url.startsWith("http") || url.startsWith("https") -> {
                browserHandler.handle(context, url)
            }

            else -> {
                // 兜底：也走浏览器
                browserHandler.handle(context, url)
            }
        }
    }
}