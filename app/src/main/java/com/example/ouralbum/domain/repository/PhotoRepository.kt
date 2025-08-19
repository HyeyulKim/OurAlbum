package com.example.ouralbum.domain.repository

import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.model.PhotoDetail
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<Photo>>
    fun getPhotosByCurrentUser(): Flow<List<Photo>>
    fun getBookmarkedPhotos(): Flow<List<Photo>>

    // 상세/수정/삭제용 추가
    fun getPhotoDetailById(photoId: String): Flow<PhotoDetail?>
    suspend fun updatePhoto(photoId: String, title: String, content: String)
    suspend fun deletePhoto(photoId: String)

    suspend fun toggleBookmark(photoId: String)

}
