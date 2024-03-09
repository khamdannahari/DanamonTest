package com.android.khamdan.di

import android.app.Application
import android.content.Context
import com.android.khamdan.util.CurrentUserPreference
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

    @Provides
    @Singleton
    fun provideCurrentUserPreference(context: Context): CurrentUserPreference =
        CurrentUserPreference(context)
}
