package com.example.ouralbum.data.mapper

import com.example.ouralbum.domain.model.Photo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPhoto(): Photo? = runCatching {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val bookmarkedBy = get("bookmarkedBy") as? List<*> ?: emptyList<Any>()

    Photo(
        id = id,
        title = getString("title") ?: "",
        date = getString("date") ?: "",
        imageUrl = getString("imageUrl") ?: "",
        isBookmarked = currentUserId != null && bookmarkedBy.contains(currentUserId)
    )
}.getOrNull()
