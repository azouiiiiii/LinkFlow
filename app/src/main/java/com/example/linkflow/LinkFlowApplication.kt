package com.example.linkflow

import android.app.Application
import com.example.linkflow.reminder.NotificationHelper

class LinkFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
