package com.example.ouralbum.data.mapper

import com.example.ouralbum.domain.model.Photo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPhoto(): Photo? = runCatching {
    val ts = getTimestamp("createdAt") ?: Timestamp.now()

    Photo(
        id = id,
        title = getString("title").orEmpty(),
        content = getString("content").orEmpty(),
        date = getString("date").orEmpty(),
        imageUrl = getString("imageUrl").orEmpty(),
        createdAt = ts.toDate().time,
        authorName = getString("authorName"),            // 닉네임
        authorPhotoUrl = getString("authorPhotoUrl")     // 프로필
    )
}.getOrNull()
