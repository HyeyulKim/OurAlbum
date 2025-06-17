package com.example.ouralbum.data.repository

import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor() : PhotoRepository {
    private val photos = mutableListOf(
        Photo("1", "여행 사진", "2025-06-10", "https://via.placeholder.com/600x400", false),
        Photo("2", "가족 사진", "2025-06-11", "https://via.placeholder.com/600x400", false)
    )

    override fun getPhotos(): Flow<List<Photo>> = flow {
        emit(photos)
    }

    override suspend fun toggleBookmark(photoId: String) {
        val index = photos.indexOfFirst { it.id == photoId }
        if (index != -1) {
            val current = photos[index]
            photos[index] = current.copy(isBookmarked = !current.isBookmarked)
        }
    }
}
