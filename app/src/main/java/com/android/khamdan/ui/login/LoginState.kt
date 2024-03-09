package com.android.khamdan.ui.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.android.khamdan.data.user.User
import com.android.khamdan.util.Event

data class LoginState(
    val currentUser: User? = null,
    val email: String = "",
    val password: String = "",
    val errorMessageEvent: Event<String?> = Event(null),
    val successLoginEvent: Event<Boolean?> = Event(null)
) {
    val isDataValid: Boolean
        get() = emailError == null &&
                passwordError == null

    val generatedErrorMessageEvent: Event<String?>
        get() = Event(emailError ?: passwordError)

    private val emailError: String?
        get() = when {
            email.isBlank() -> "Email cannot be empty"
            !EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email address"
            else -> null
        }

    private val passwordError: String?
        get() = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }

}

