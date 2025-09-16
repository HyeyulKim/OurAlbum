package com.example.ouralbum.data.repository

import com.example.ouralbum.data.mapper.toPhoto
import com.example.ouralbum.data.mapper.toPhotoDetail
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.domain.model.PhotoDetail
import com.example.ouralbum.domain.repository.PhotoRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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

    private fun photosRef() = firestore.collection("photos")
    private fun userBookmarksRef(uid: String) =
        firestore.collection("users").document(uid).collection("bookmarks")

    override fun getAllPhotos(): Flow<List<Photo>> = callbackFlow {
        val listener = photosRef()
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

        val listener = photosRef()
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val photos = snapshot?.documents?.mapNotNull { it.toPhoto() } ?: emptyList()
                trySend(photos).isSuccess
            }
        awaitClose { listener.remove() }
    }

    // users/{uid}/bookmarks 문서(id=photoId) -> photos 컬렉션에서 배치 조회(chunks)
    override fun getBookmarkedPhotos(): Flow<List<Photo>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }

        val listener = userBookmarksRef(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { bookmarksSnap, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                // 1) 북마크 id와 "북마크 시간" 맵 생성
                val bookmarkTimes: Map<String, Long> =
                    bookmarksSnap?.documents?.associate { d ->
                        val photoId = d.id
                        val t = d.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                        photoId to t
                    } ?: emptyMap()

                val ids = bookmarkTimes.keys.toList()
                if (ids.isEmpty()) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                // 2) photos에서 chunk로 가져오기(whereIn 제한 방지)
                val chunks = ids.chunked(10)
                val tasks = chunks.map { chunk ->
                    photosRef()
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                }

                Tasks.whenAllSuccess<QuerySnapshot>(tasks)
                    .addOnSuccessListener { results ->
                        // 3) 병합 후 "북마크한 시점"으로 최신순 정렬
                        val merged = results
                            .flatMap { it.documents }
                            .mapNotNull { it.toPhoto() }
                            .sortedByDescending { p -> bookmarkTimes[p.id] ?: 0L }
                            // 동시간대 동률 시 업로드 시간으로 2차 정렬
                            .sortedWith(compareByDescending<Photo> { bookmarkTimes[it.id] ?: 0L }
                            .thenByDescending { it.createdAt })

                        trySend(merged).isSuccess
                    }
                    .addOnFailureListener { t -> close(t) }
            }

        awaitClose { listener.remove() }
    }


    // 상세
    override fun getPhotoDetailById(photoId: String): Flow<PhotoDetail?> = callbackFlow {
        val docRef = photosRef().document(photoId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            trySend(snapshot?.toPhotoDetail()).isSuccess
        }
        awaitClose { listener.remove() }
    }

    // 수정 (작성자만)
    override suspend fun updatePhoto(photoId: String, title: String, content: String) {
        val uid = firebaseAuth.currentUser?.uid ?: error("로그인 필요")
        val docRef = photosRef().document(photoId)
        val ownerId = docRef.get().await().getString("userId") ?: ""
        require(ownerId == uid) { "본인만 수정할 수 있습니다." }

        docRef.update(
            mapOf(
                "title" to title,
                "content" to content,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    // 삭제 (Storage 이미지 → Firestore 문서 순으로)
    override suspend fun deletePhoto(photoId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: error("로그인 필요")
        val docRef = photosRef().document(photoId)
        val snap = docRef.get().await()
        val ownerId = snap.getString("userId") ?: ""
        require(ownerId == uid) { "본인만 삭제할 수 있습니다." }

        val storagePath = snap.getString("storagePath")
        val imageUrl = snap.getString("imageUrl")

        // 1) Storage 삭제 (경로 없으면 과거 규칙 추론)
        try {
            when {
                !storagePath.isNullOrBlank() ->
                    storage.reference.child(storagePath).delete().await()
                !imageUrl.isNullOrBlank() ->
                    storage.getReferenceFromUrl(imageUrl).delete().await()
                else -> {
                    val guess = "photos/$ownerId/$photoId.jpg"
                    storage.reference.child(guess).delete().await()
                }
            }
        } catch (_: Exception) {
            // 파일 없을 수도 있으니 문서 삭제는 진행
        }

        // 2) Firestore 문서 삭제
        docRef.delete().await()
    }

    // photos 문서 수정 금지. users/{uid}/bookmarks/{photoId}에 생성/삭제만 수행
    override suspend fun toggleBookmark(photoId: String) {
        require(photoId.isNotBlank()) { "invalid photoId" }
        val uid = firebaseAuth.currentUser?.uid ?: error("로그인 필요")

        val bookmarkDoc = userBookmarksRef(uid).document(photoId)
        firestore.runTransaction { tx ->
            val snap = tx.get(bookmarkDoc)
            if (snap.exists()) {
                tx.delete(bookmarkDoc)
            } else {
                tx.set(
                    bookmarkDoc,
                    mapOf(
                        "photoId" to photoId,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }
        }.await()
    }
}
