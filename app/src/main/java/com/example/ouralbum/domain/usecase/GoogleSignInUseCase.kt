package com.example.ouralbum.domain.usecase

import android.util.Log
import com.example.ouralbum.domain.repository.AuthRepository
import com.example.ouralbum.presentation.screen.login.LoginViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(idToken: String): Flow<LoginViewModel.LoginResult> {
        Log.d("GoogleSignInUseCase", "invoke called with idToken: $idToken")
        return authRepository.signInWithGoogle(idToken)
    }
}
