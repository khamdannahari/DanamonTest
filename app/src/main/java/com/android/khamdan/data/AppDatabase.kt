package com.android.khamdan.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.khamdan.data.user.UserDao
import com.android.khamdan.data.user.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
