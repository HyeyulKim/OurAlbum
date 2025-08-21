package com.example.ouralbum.data.repository

import com.example.ouralbum.data.mapper.toPhoto
import com.example.ouralbum.data.mapper.toPhotoDetail
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.model.PhotoDetail
import com.example.ouralbum.domain.repository.PhotoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage
) : PhotoRepository {

    override fun getAllPhotos(): Flow<List<Photo>> = callbackFlow {
        val listener = firestore.collection("photos")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos).isSuccess
            }
        awaitClose { listener.remove() }
    }

    override fun getPhotosByCurrentUser(): Flow<List<Photo>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val listener = firestore.collection("photos")
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos)
            }
        awaitClose { listener.remove() }
    }

    override fun getBookmarkedPhotos(): Flow<List<Photo>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }
        val listener = firestore.collection("photos")
            .whereArrayContains("bookmarkedBy", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos)
            }
        awaitClose { listener.remove() }
    }

    // 상세
    override fun getPhotoDetailById(photoId: String): Flow<PhotoDetail?> = callbackFlow {
        val docRef = firestore.collection("photos").document(photoId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            trySend(snapshot?.toPhotoDetail())
        }
        awaitClose { listener.remove() }
    }

    // 수정 (작성자만)
    override suspend fun updatePhoto(photoId: String, title: String, content: String) {
        val uid = firebaseAuth.currentUser?.uid ?: error("로그인 필요")
        val docRef = firestore.collection("photos").document(photoId)
        val ownerId = docRef.get().await().getString("userId") ?: ""
        require(ownerId == uid) { "본인만 수정할 수 있습니다." }
        docRef.update(mapOf("title" to title, "content" to content)).await()
    }

    // 삭제 (Storage 이미지 → Firestore 문서 순으로)
    override suspend fun deletePhoto(photoId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: error("로그인 필요")
        val docRef = firestore.collection("photos").document(photoId)
        val snap = docRef.get().await()
        val ownerId = snap.getString("userId") ?: ""
        require(ownerId == uid) { "본인만 삭제할 수 있습니다." }

        val storagePath = snap.getString("storagePath")
        val imageUrl = snap.getString("imageUrl") // 없으면 storagePath로만 처리

        // 1) Storage 삭제
        try {
            when {
                !storagePath.isNullOrBlank() -> {
                    storage.reference.child(storagePath).delete().await()
                }
                !imageUrl.isNullOrBlank() -> {
                    // URL로 바로 참조 얻어 삭제
                    storage.getReferenceFromUrl(imageUrl).delete().await()
                }
                else -> {
                    // 경로가 전혀 없으면, 과거 업로더 규칙대로 추론 시도
                    // "photos/{userId}/{photoId}.jpg"
                    val guess = "photos/$ownerId/$photoId.jpg"
                    storage.reference.child(guess).delete().await()
                }
            }
        } catch (_: Exception) {
            // 이미지가 이미 없거나 경로가 바뀐 경우도 있으니 문서 삭제는 진행
        }

        // 2) Firestore 문서 삭제
        docRef.delete().await()
    }

    override suspend fun toggleBookmark(photoId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val docRef = firestore.collection("photos").document(photoId)
        firestore.runTransaction { tx ->
            val snap = tx.get(docRef)
            val bookmarkedBy = (snap.get("bookmarkedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            val updated = if (uid in bookmarkedBy) bookmarkedBy - uid else bookmarkedBy + uid
            tx.update(docRef, "bookmarkedBy", updated)
        }.await()
    }
}
