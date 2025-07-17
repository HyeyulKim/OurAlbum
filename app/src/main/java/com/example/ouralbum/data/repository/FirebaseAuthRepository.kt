package com.example.ouralbum.data.repository

import com.example.ouralbum.domain.repository.AuthRepository
import com.example.ouralbum.presentation.screen.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun signInWithGoogle(idToken: String): Flow<LoginViewModel.LoginResult> = flow {
        try {
            // 자격 증명 생성
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Firebase 인증 시도 (suspend 함수로 변환)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null) {
                val userInfo = LoginViewModel.UserInfo(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: ""
                )
                emit(LoginViewModel.LoginResult.Success(userInfo))
            } else {
                emit(LoginViewModel.LoginResult.Failure("사용자 정보를 불러올 수 없습니다."))
            }
        } catch (e: Exception) {
            emit(LoginViewModel.LoginResult.Failure(e.message ?: "로그인 실패"))
        }
    }
}
