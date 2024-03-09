package com.android.khamdan.di

import com.android.khamdan.data.user.UserDao
import com.android.khamdan.data.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao) =
        UserRepository(userDao)

}
