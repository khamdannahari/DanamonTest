package com.android.khamdan.data.photo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: Int = -1,
    val albumId: Int = -1,
    val title: String = "",
    val url: String = "",
    val thumbnailUrl: String = ""
) : Parcelable