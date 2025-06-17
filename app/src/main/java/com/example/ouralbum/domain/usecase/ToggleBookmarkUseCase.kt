package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.repository.PhotoRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(photoId: String) = repository.toggleBookmark(photoId)
}
