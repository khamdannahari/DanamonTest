package com.android.khamdan.ui.edituser

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
class EditUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _editUserState = MutableStateFlow(EditUserState())
    val editUserState: StateFlow<EditUserState> get() = _editUserState.asStateFlow()

    fun setUser(user: User) {
        _editUserState.update {
            it.copy(
                id = if (user.id > 0) user.id else it.id,
                username = user.username,
                email = user.email,
                password = user.password,
                role = user.role,
            )
        }
    }

    fun update() {
        if (_editUserState.value.isDataValid) {
            updateUser()
        } else {
            _editUserState.update {
                it.copy(errorMessageEvent = it.generatedErrorMessageEvent)
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            _editUserState.value.run {
                userRepository.updateUser(
                    User(
                        id = id,
                        username = username,
                        email = email,
                        password = password,
                        role = role,
                    )
                ).collect { result ->
                    result.onSuccess {
                        _editUserState.update {
                            it.copy(successUpdateEvent = Event(true))
                        }
                    }.onFailure { resultFailure ->
                        _editUserState.update {
                            it.copy(errorMessageEvent = Event(resultFailure.message))
                        }
                    }
                }
            }
        }
    }

}