package com.android.khamdan.data.photo

import com.android.khamdan.data.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PhotoRepository(private val apiService: ApiService) {

    fun getPhotos(page: Int): Flow<List<Photo>> = flow {
        val provinces = apiService.getPhotos(page)
        emit(provinces)
    }.flowOn(Dispatchers.IO)

}