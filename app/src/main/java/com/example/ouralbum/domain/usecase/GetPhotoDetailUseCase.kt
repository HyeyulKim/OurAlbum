package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.model.PhotoDetail
import com.example.ouralbum.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(photoId: String): Flow<PhotoDetail?> = repo.getPhotoDetailById(photoId)
}