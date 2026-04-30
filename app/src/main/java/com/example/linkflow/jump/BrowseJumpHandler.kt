// 浏览器跳转

package com.example.linkflow.jump

import android.content.Context
import android.content.Intent
import android.net.Uri

class BrowserJumpHandler : JumpHandler {

    override fun handle(context: Context, url: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        context.startActivity(intent)
    }
}