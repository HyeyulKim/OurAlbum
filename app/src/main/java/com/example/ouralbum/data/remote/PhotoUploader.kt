package com.example.ouralbum.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PhotoUploader @Inject constructor(
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun uploadPhoto(
        imageUri: Uri,
        title: String,
        content: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // 최신 프로필 확보
            auth.currentUser?.getIdToken(true)?.await()
            auth.currentUser?.reload()?.await()

            val user = auth.currentUser ?: throw IllegalStateException("로그인 필요")
            val userId = user.uid

            // 구글 닉네임/프로필 안전 추출
            val googleProviderId = "google.com"
            val provider = user.providerData.firstOrNull { it.providerId == googleProviderId }
            val resolvedName = user.displayName ?: provider?.displayName ?: ""
            val resolvedPhotoUrl = (user.photoUrl ?: provider?.photoUrl)?.toString() ?: ""

            // Firestore 문서 ID
            val photoDocRef = firestore.collection("photos").document()
            val photoId = photoDocRef.id

            // Storage 경로
            val storagePath = "photos/$userId/$photoId.jpg"
            val storageRef = storage.reference.child(storagePath)

            // 1) 업로드
            storageRef.putFile(imageUri).await()

            // 2) 다운로드 URL
            val imageUrl = storageRef.downloadUrl.await().toString()

            // 3) 문서 저장 (작성자 메타 포함)
            val photoData = hashMapOf(
                "title" to title,
                "content" to content,
                "date" to getCurrentDate(),
                "imageUrl" to imageUrl,
                "storagePath" to storagePath,
                "userId" to userId,
                "authorName" to resolvedName,             // 닉네임 저장
                "authorPhotoUrl" to resolvedPhotoUrl,     // 프로필 저장
                "createdAt" to FieldValue.serverTimestamp()
            )

            photoDocRef.set(photoData).await()
            onSuccess()
        } catch (e: Exception) {
            Log.e("UploadError", "Firebase 업로드 실패", e)
            onFailure(e)
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return formatter.format(Date())
    }
}
