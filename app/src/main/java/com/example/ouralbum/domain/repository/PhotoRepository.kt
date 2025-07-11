package com.example.ouralbum.domain.repository

import com.example.ouralbum.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<Photo>>
    fun getPhotosByCurrentUser(): Flow<List<Photo>>
    fun getBookmarkedPhotos(): Flow<List<Photo>>

    suspend fun toggleBookmark(photoId: String)
}
