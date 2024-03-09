package com.android.khamdan.data

import com.android.khamdan.BuildConfig
import com.android.khamdan.data.photo.Photo
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("_page") page: Int = 1,
        @Query("_limit") limit: Int = 10,
        @Query("_sort") sort: String = "albumId",
    ): List<Photo>

}