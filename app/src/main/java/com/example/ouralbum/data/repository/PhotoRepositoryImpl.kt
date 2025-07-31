package com.example.ouralbum.data.repository

import com.example.ouralbum.data.mapper.toPhoto
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.repository.PhotoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PhotoRepository {

    /** 🔹 전체 사진 실시간 스트림 */
    override fun getAllPhotos(): Flow<List<Photo>> = callbackFlow {
        val listener = firestore.collection("photos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // 필요하다면 close(error) 로 흐름 종료
                    return@addSnapshotListener
                }
                val photos = snapshot?.documents
                    ?.mapNotNull { it.toPhoto() }
                    ?: emptyList()
                trySend(photos).isSuccess
            }

        // 콜드 스트림 정리
        awaitClose { listener.remove() }
    }

    /** 🔹 현재 사용자 사진만 */
    override fun getPhotosByCurrentUser(): Flow<List<Photo>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: return@callbackFlow
        val listener = firestore.collection("photos")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, _ ->
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos)
            }
        awaitClose { listener.remove() }
    }

    /** 🔹 내가 북마크한 사진만 */
    override fun getBookmarkedPhotos(): Flow<List<Photo>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: return@callbackFlow
        val listener = firestore.collection("photos")
            .whereArrayContains("bookmarkedBy", uid)
            .addSnapshotListener { snapshot, _ ->
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos)
            }
        awaitClose { listener.remove() }
    }

    /** 🔹 북마크 토글 */
    override suspend fun toggleBookmark(photoId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val docRef = firestore.collection("photos").document(photoId)

        firestore.runTransaction { tx ->
            val snap = tx.get(docRef)
            val bookmarkedBy = snap.get("bookmarkedBy") as? List<String> ?: emptyList()
            val updated =
                if (uid in bookmarkedBy) bookmarkedBy - uid
                else bookmarkedBy + uid
            tx.update(docRef, "bookmarkedBy", updated)
        }.await()
    }
}
