package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.repository.PhotoRepository
import javax.inject.Inject

class DeletePhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    suspend operator fun invoke(photoId: String) {
        repo.deletePhoto(photoId)
    }
}