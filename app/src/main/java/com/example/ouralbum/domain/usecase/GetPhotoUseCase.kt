package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val repository: PhotoRepository
) {
    operator fun invoke(): Flow<List<Photo>> {
        return repository.getAllPhotos()
    }
}
