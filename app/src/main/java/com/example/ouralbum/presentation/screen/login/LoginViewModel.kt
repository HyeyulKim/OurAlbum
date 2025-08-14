package com.example.ouralbum.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.GoogleSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            googleSignInUseCase(idToken)
                .onStart {
                    _loginState.value = LoginState.Loading
                }
                .catch { e ->
                    _loginState.value = LoginState.Failure(e.message ?: "로그인 실패")
                }
                .collect { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            _loginState.value = LoginState.Success(result.userInfo)
                            _userInfo.value = result.userInfo
                        }

                        is LoginResult.Failure -> {
                            _loginState.value = LoginState.Failure(result.errorMessage)
                        }
                    }
                }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
        _userInfo.value = null
    }

    sealed interface LoginState {
        object Idle : LoginState
        object Loading : LoginState
        data class Success(val userInfo: UserInfo) : LoginState
        data class Failure(val message: String?) : LoginState
    }

    sealed class LoginResult {
        data class Success(val userInfo: UserInfo) : LoginResult()
        data class Failure(val errorMessage: String) : LoginResult()
    }

    data class UserInfo(
        val id: String,
        val email: String,
        val displayName: String
    )
}
