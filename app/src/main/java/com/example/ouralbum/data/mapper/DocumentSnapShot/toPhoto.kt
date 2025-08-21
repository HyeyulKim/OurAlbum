package com.example.ouralbum.data.mapper

import com.example.ouralbum.domain.model.Photo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Firestore 문서를 목록용 Photo로 매핑.
 * - 현재 로그인 유저 기준(isBookmarked) 계산 포함
 */
fun DocumentSnapshot.toPhoto(): Photo? = runCatching {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val bookmarkedBy = (get("bookmarkedBy") as? List<*>)?.filterIsInstance<String>().orEmpty()
    val ts = getTimestamp("createdAt")
    Photo(
        id = id,
        title = getString("title").orEmpty(),
        content = getString("content").orEmpty(),
        date = getString("date").orEmpty(),
        imageUrl = getString("imageUrl").orEmpty(),
        isBookmarked = currentUserId != null && currentUserId in bookmarkedBy,
        createdAt = ts?.toDate()?.time ?: 0L
    )
}.getOrNull()
