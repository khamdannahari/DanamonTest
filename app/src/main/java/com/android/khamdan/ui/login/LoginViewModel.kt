package com.android.khamdan.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.khamdan.data.user.UserRepository
import com.android.khamdan.util.CurrentUserPreference
import com.android.khamdan.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserPreference: CurrentUserPreference
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> get() = _loginState.asStateFlow()

    init {
        _loginState.update {
            it.copy(currentUser = currentUserPreference.getCurrentUser())
        }
    }

    fun updateForm(email: String, password: String) {
        _loginState.update { it.copy(email = email, password = password) }
    }

    fun login() {
        if (_loginState.value.isDataValid) {
            checkUser()
        } else {
            triggerErrorMessageEvent()
        }
    }

    private fun checkUser() {
        _loginState.value.run {
            userRepository.getUserByEmailAndPassword(
                email = email,
                password = password,
            ).catch { exception ->
                _loginState.update { it.copy(errorMessageEvent = Event(exception.message)) }
            }.onEach { user ->
                if (user != null) {
                    currentUserPreference.saveCurrentUser(user)
                    _loginState.update { it.copy(currentUser = user) }
                } else {
                    _loginState.update { state ->
                        state.copy(errorMessageEvent = Event("User not found"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun triggerErrorMessageEvent() {
        _loginState.update { it.copy(errorMessageEvent = it.generatedErrorMessageEvent) }
    }
}