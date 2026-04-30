package com.example.linkflow.jump

import android.content.Context

interface JumpHandler {
    fun handle(context: Context, url: String)
}