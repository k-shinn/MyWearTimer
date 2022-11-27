package com.kei.myweartimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kei.myweartimer.presentation.MainActivity

class MyForegroundService : Service() {

    private lateinit var notificationManager: NotificationManager

    private val localBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        generateNotification()

        return Service.START_NOT_STICKY
    }

    fun startTimer() {
        startService(Intent(applicationContext, MyForegroundService::class.java))
    }

    fun stopTimer() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun generateNotification() {
        // MEMO: 取り敢えずforegroundService用に通知を置くだけ

        // createChannel
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, "Timer", NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        // generateNotification
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notification: Notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("title")
            .setContentText("text")
            .setSmallIcon(R.drawable.baseline_timer_24)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    inner class LocalBinder : Binder() {
        internal val myForegroundService: MyForegroundService
            get() = this@MyForegroundService
    }

    companion object {
        private const val NOTIFICATION_ID = 12345678
        private const val NOTIFICATION_CHANNEL_ID = "my_wear_timer_channel_01"
    }
}