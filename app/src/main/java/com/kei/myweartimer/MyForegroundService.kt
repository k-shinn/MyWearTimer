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
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.kei.myweartimer.data.DataStore
import com.kei.myweartimer.presentation.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyForegroundService : LifecycleService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataStoreEntryPoint {
        fun dataStore(): DataStore
    }

    private lateinit var dataStore: DataStore
    private lateinit var notificationManager: NotificationManager

    private val localBinder = LocalBinder()

    private var timerJob: Job? = null


    override fun onCreate() {
        super.onCreate()

        val entryPoint =
            EntryPointAccessors.fromApplication(applicationContext, DataStoreEntryPoint::class.java)
        dataStore = entryPoint.dataStore()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return localBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        generateNotification()
        startTimerLoop()
        return Service.START_NOT_STICKY
    }

    private fun startTimerLoop() {
        timerJob = lifecycleScope.launch {
            dataStore.setActiveTimer(true)
            dataStore.setStartTime(System.currentTimeMillis())
            timerLoop()
        }
    }

    private suspend fun timerLoop() {
        var currentTime = 0
        while (true) {
            dataStore.setValueTime(currentTime)
            delay(1000)
            currentTime++
            if (currentTime > 45) currentTime = 0
        }
    }

    fun startTimer() {
        startService(Intent(applicationContext, MyForegroundService::class.java))
    }

    fun stopTimer() {
        lifecycleScope.launch {
            dataStore.setActiveTimer(false)
        }
        timerJob?.cancel()
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