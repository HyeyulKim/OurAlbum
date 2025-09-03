package com.example.ouralbum.data.mapper

import com.example.ouralbum.domain.model.Photo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPhoto(): Photo? = runCatching {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val ts = getTimestamp("createdAt")
    Photo(
        id = id,
        title = getString("title").orEmpty(),
        content = getString("content").orEmpty(),
        date = getString("date").orEmpty(),
        imageUrl = getString("imageUrl").orEmpty(),
        createdAt = ts?.toDate()?.time ?: 0L
    )
}.getOrNull()
