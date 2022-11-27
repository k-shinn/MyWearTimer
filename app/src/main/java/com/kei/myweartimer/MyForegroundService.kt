package com.kei.myweartimer

import android.app.NotificationChannel
import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyForegroundService : Service() {

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
generateNotification()

    }

    private fun generateNotification() {
        NotificationChannel(π)
    }
}