package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(): Flow<List<Photo>> {
        return photoRepository.getBookmarkedPhotos()
    }
}
