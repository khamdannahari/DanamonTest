package com.android.khamdan.ui.register

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.android.khamdan.util.Event

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val errorMessageEvent: Event<String?> = Event(null),
    val successRegisterEvent: Event<Boolean?> = Event(null)
) {
    val isDataValid: Boolean
        get() = usernameError == null &&
                emailError == null &&
                passwordError == null &&
                roleError == null

    val generatedErrorMessageEvent: Event<String?>
        get() = Event(
            usernameError ?: emailError ?: roleError ?: passwordError
        )

    private val usernameError: String?
        get() = when {
            username.isBlank() -> "Username cannot be empty"
            else -> null
        }

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

    private val roleError: String?
        get() = if (role.isBlank()) {
            "Role must be selected"
        } else {
            null
        }

}

