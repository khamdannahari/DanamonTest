package com.android.khamdan.util

import android.content.Context
import com.android.khamdan.data.user.User

class CurrentUserPreference(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        CURRENT_USER_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun saveCurrentUser(currentUser: User) {
        with(sharedPreferences.edit()) {
            putLong(USER_ID_KEY, currentUser.id)
            putString(USERNAME_KEY, currentUser.username)
            putString(EMAIL_KEY, currentUser.email)
            putString(PASSWORD_KEY, currentUser.password)
            putString(ROLE_KEY, currentUser.role)
            apply()
        }
    }

    fun getCurrentUser(): User? {
        val userId = sharedPreferences.getLong(USER_ID_KEY, -1)
        val username = sharedPreferences.getString(USERNAME_KEY, null)
        val email = sharedPreferences.getString(EMAIL_KEY, null)
        val password = sharedPreferences.getString(PASSWORD_KEY, null)
        val role = sharedPreferences.getString(ROLE_KEY, null)

        return if (userId != -1L && username != null && email != null && password != null && role != null) {
            User(userId, username, email, password, role)
        } else {
            null
        }
    }

    fun deleteCurrentUser() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val CURRENT_USER_PREFERENCES_NAME = "current_user_preferences"
        private const val USER_ID_KEY = "user_id"
        private const val USERNAME_KEY = "username"
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val ROLE_KEY = "role"
    }
}
