package com.kei.myweartimer

import android.content.Context
import com.kei.myweartimer.data.DataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext application: Context): DataStore {
        return DataStore(application)
    }
}