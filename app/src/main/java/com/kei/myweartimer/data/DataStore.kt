package com.kei.myweartimer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore (
    private val context: Context
) {
    companion object {
        private const val DATASTORE_NAME = "my_wear_timer_datastore"
        private val ACTIVE_TIMER_KEY = booleanPreferencesKey("active_timer_key")
        private val VALUE_TIME_KEY = intPreferencesKey("value_time_key")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATASTORE_NAME
    )

    val activeTimer: Flow<Boolean> = context.dataStore.data.map {
        it[ACTIVE_TIMER_KEY] ?: false
    }

    val valueTime: Flow<Int> = context.dataStore.data.map {
        it[VALUE_TIME_KEY] ?: 0
    }

    suspend fun setActiveTimer(activeTimer: Boolean) {
        context.dataStore.edit {
            it[ACTIVE_TIMER_KEY] = activeTimer
        }
    }

    suspend fun setValueTime(time: Int) {
        context.dataStore.edit {
            it[VALUE_TIME_KEY] = time
        }
    }
}
