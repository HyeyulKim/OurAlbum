package com.example.ouralbum.data.mapper

import com.example.ouralbum.domain.model.PhotoDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPhotoDetail(): PhotoDetail? = runCatching {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    PhotoDetail(
        id = id,
        title = getString("title") ?: "",
        content = getString("content") ?: "",
        date = getString("date") ?: "",
        imageUrl = getString("imageUrl") ?: "",
        userId = getString("userId") ?: "",
        storagePath = getString("storagePath") // 선택 저장 필드(없어도 동작)
    )
}.getOrNull()
