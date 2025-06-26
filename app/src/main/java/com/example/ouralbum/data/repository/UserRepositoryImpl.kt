package com.example.ouralbum.data.repository

import com.example.ouralbum.domain.model.User
import com.example.ouralbum.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(
                id = it.uid,
                name = it.displayName ?: "이름 없음",
                email = it.email ?: "이메일 없음"
            )
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
