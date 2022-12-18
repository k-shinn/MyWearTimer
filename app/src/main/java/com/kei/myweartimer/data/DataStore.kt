package com.kei.myweartimer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore(
    private val context: Context
) {
    companion object {
        private const val DATASTORE_NAME = "my_wear_timer_datastore"
        private val TIMER_RUNNING_KEY = booleanPreferencesKey("timer_running_key")
        private val START_TIME_KEY = longPreferencesKey("start_time_key")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATASTORE_NAME
    )

    val timerRunning: Flow<Boolean> = context.dataStore.data.map {
        it[TIMER_RUNNING_KEY] ?: false
    }

    val startTime: Flow<Long> = context.dataStore.data.map {
        it[START_TIME_KEY] ?: 0
    }

    suspend fun setTimerRunning(timerRunning: Boolean) {
        context.dataStore.edit {
            it[TIMER_RUNNING_KEY] = timerRunning
        }
    }

    suspend fun setStartTime(startTime: Long) {
        context.dataStore.edit {
            it[START_TIME_KEY] = startTime
        }
    }
}
