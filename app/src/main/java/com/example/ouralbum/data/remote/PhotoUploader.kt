package com.example.ouralbum.data.remote

import android.net.Uri
import android.util.Log
import com.example.ouralbum.domain.model.Photo
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
            val auth = FirebaseAuth.getInstance()
            auth.currentUser?.getIdToken(true)?.await() // 인증 강제 refresh

            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: throw IllegalStateException("로그인 필요")

            // Firestore 문서 ID 미리 생성
            val photoDocRef = firestore.collection("photos").document()
            val photoId = photoDocRef.id

            // Storage 경로: photos/{userId}/{photoId}.jpg
            val storageRef = storage.reference.child("photos/$userId/$photoId.jpg")

            // 1. Storage에 업로드
            storageRef.putFile(imageUri).await()

            // 2. 다운로드 URL 받아오기
            val imageUrl = storageRef.downloadUrl.await().toString()

            // 3. Firestore에 사진 정보 저장
            val photoData = hashMapOf(
                "title" to title,
                "content" to content,
                "date" to getCurrentDate(),
                "imageUrl" to imageUrl,
                "userId" to userId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            photoDocRef.set(photoData).await()
            onSuccess()
        } catch (e: Exception) {
            Log.e("UploadError", "Firebase Storage 업로드 실패", e)
            onFailure(e)
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return formatter.format(Date())
    }
}
