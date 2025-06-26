package com.example.ouralbum.domain.repository

import com.example.ouralbum.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun logout()
}
