package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.repository.PhotoRepository
import javax.inject.Inject

class UpdatePhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    suspend operator fun invoke(photoId: String, title: String, content: String) {
        repo.updatePhoto(photoId, title, content)
    }
}