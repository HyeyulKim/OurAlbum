package com.example.ouralbum.data.repository

import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor() : PhotoRepository {

    private val mockUserId = "user123" // 임시 사용자 ID

    private val allPhotos = listOf(
        Photo(id = "1", title = "전체 사진 1", date = "2025.06.17", imageUrl = "https://picsum.photos/id/1015/600/400", isBookmarked = false),
        Photo(id = "2", title = "전체 사진 2", date = "2025.06.16", imageUrl = "https://picsum.photos/id/1021/600/400", isBookmarked = false),
        Photo(id = "3", title = "전체 사진 3", date = "2025.06.15", imageUrl = "https://picsum.photos/id/1025/600/400", isBookmarked = false),
        Photo(id = "4", title = "전체 사진 4", date = "2025.06.14", imageUrl = "https://picsum.photos/id/1035/600/400", isBookmarked = true),
        Photo(id = "5", title = "전체 사진 5", date = "2025.06.13", imageUrl = "https://picsum.photos/id/1041/600/400", isBookmarked = false),

        Photo(id = "6", title = "내 사진 1", date = "2025.06.12", imageUrl = "https://picsum.photos/id/1052/600/400", isBookmarked = true),
        Photo(id = "7", title = "내 사진 2", date = "2025.06.11", imageUrl = "https://picsum.photos/id/1062/600/400", isBookmarked = false),
        Photo(id = "8", title = "내 사진 3", date = "2025.06.10", imageUrl = "https://picsum.photos/id/1074/600/400", isBookmarked = false),
        Photo(id = "9", title = "내 사진 4", date = "2025.06.09", imageUrl = "https://picsum.photos/id/1084/600/400", isBookmarked = true),
        Photo(id = "10", title = "내 사진 5", date = "2025.06.08", imageUrl = "https://picsum.photos/id/109/600/400", isBookmarked = false),
    )


    override fun getAllPhotos(): Flow<List<Photo>> = flow {
        emit(allPhotos)
    }

    override fun getPhotosByCurrentUser(): Flow<List<Photo>> = flow {
        // 실제 구현에서는 사용자 ID 기준 필터링 필요
        val userPhotos = allPhotos.filter { it.id != "1" } // 예: 사용자 사진만 필터링
        emit(userPhotos)
    }

    override suspend fun toggleBookmark(photoId: String) {
        // TODO: 실제 북마크 토글 로직 구현 필요
    }
}
