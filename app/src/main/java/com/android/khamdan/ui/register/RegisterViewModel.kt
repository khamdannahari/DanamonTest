package com.android.khamdan.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.khamdan.data.user.User
import com.android.khamdan.data.user.UserRepository
import com.android.khamdan.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> get() = _registerState.asStateFlow()

    fun updateState(state: RegisterState) {
        _registerState.value = state
    }

    fun register() {
        if (_registerState.value.isDataValid) {
            insertUser()
        } else {
            _registerState.update {
                it.copy(errorMessageEvent = it.generatedErrorMessageEvent)
            }
        }
    }

    private fun insertUser() {
        viewModelScope.launch {
            _registerState.value.run {
                userRepository.insertUser(
                    User(
                        username = username,
                        email = email,
                        password = password,
                        role = role,
                    )
                ).collect { result ->
                    result.onSuccess {
                        _registerState.update {
                            it.copy(successRegisterEvent = Event(true))
                        }
                    }.onFailure { resultFailure ->
                        _registerState.update {
                            it.copy(errorMessageEvent = Event(resultFailure.message))
                        }
                    }
                }
            }
        }
    }
}