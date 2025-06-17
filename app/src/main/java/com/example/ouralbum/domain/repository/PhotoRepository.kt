package com.example.ouralbum.domain.repository

import com.example.ouralbum.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
    suspend fun toggleBookmark(photoId: String)
}