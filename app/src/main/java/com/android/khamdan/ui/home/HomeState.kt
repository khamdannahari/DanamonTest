package com.android.khamdan.ui.home

import com.android.khamdan.data.photo.Photo
import com.android.khamdan.data.user.User
import com.android.khamdan.util.Event

data class HomeState(
    val currentUser: User? = null,
    val photos: List<Photo> = emptyList(),
    val users: List<User> = emptyList(),
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val errorMessageEvent: Event<String?> = Event(null),
    val successLoginEvent: Event<Boolean?> = Event(null)
)
