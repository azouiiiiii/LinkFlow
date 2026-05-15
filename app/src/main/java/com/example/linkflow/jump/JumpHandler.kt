package com.example.linkflow.jump

import android.content.Context
import android.content.Intent
import com.example.linkflow.schedule.Schedule

interface JumpHandler {

    fun handle(
        context: Context,
        schedule: Schedule
    ): Intent?
}