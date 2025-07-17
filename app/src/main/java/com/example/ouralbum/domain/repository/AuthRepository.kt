package com.example.ouralbum.domain.repository

import com.example.ouralbum.presentation.screen.login.LoginViewModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithGoogle(idToken: String): Flow<LoginViewModel.LoginResult>
}
