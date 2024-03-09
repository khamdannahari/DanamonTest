package com.android.khamdan.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.android.khamdan.data.AppDatabase
import com.android.khamdan.data.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }
}
