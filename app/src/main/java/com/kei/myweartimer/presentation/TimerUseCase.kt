package com.kei.myweartimer.presentation

import com.kei.myweartimer.data.DataStore
import javax.inject.Inject

class TimerUseCase @Inject constructor(
    private val dataStore: DataStore
) {

    suspend fun startTimer() {
        dataStore.setTimerRunning(true)
        dataStore.setStartTime(System.currentTimeMillis())
    }

    suspend fun stopTimer() {
        dataStore.setTimerRunning(false)
    }

}
