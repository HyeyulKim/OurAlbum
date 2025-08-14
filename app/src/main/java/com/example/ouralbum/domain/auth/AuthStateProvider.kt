package com.example.ouralbum.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthStateProvider {
    val isLoggedIn: Flow<Boolean>
}
