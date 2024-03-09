package com.android.khamdan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.khamdan.data.photo.PhotoRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val currentUserPreference: CurrentUserPreference
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> get() = _homeState.asStateFlow()

    init {
        val currentUser = currentUserPreference.getCurrentUser()
        _homeState.update { it.copy(currentUser = currentUser) }

        if (currentUser?.role?.lowercase() == "normal") {
            getPhotos()
        } else {
            getUsers()
        }
    }

    fun getPhotos(currentPage: Int = 1) {
        _homeState.update { it.copy(isLoading = true, currentPage = currentPage) }

        photoRepository.getPhotos(currentPage)
            .catch { exception ->
                _homeState.update {
                    it.copy(
                        errorMessageEvent = Event(exception.message),
                        isLoading = false,
                    )
                }
            }.onEach { photos ->
                _homeState.update { it.copy(photos = photos, isLoading = false) }
            }.launchIn(viewModelScope)
    }

    private fun getUsers() = userRepository.getAllUsers()
        .catch { exception ->
            _homeState.update { it.copy(errorMessageEvent = Event(exception.message)) }
        }.onEach { users ->
            _homeState.update { it.copy(users = users) }
        }.launchIn(viewModelScope)

    fun deleteUser(id: Long, adminPassword: String) {
        viewModelScope.launch {
            val savedPassword = _homeState.value.currentUser?.password

            if (savedPassword != null &&
                savedPassword == adminPassword
            ) {
                userRepository.deleteUser(id)
            } else {
                _homeState.update {
                    it.copy(errorMessageEvent = Event("Wrong Password"))
                }
            }
        }
    }

    fun deleteCurrentUser() {
        viewModelScope.launch {
            currentUserPreference.deleteCurrentUser()
        }
    }

}