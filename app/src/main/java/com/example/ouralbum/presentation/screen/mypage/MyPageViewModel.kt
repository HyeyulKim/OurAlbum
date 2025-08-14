package com.example.ouralbum.presentation.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.model.User
import com.example.ouralbum.domain.usecase.GetCurrentUserUseCase
import com.example.ouralbum.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            val user: User? = getCurrentUserUseCase()
            if (user != null) {
                _isLoggedIn.value = true
                _uiState.value = MyPageUiState(
                    name = user.name,
                    email = user.email
                )
            } else {
                _isLoggedIn.value = false
                _uiState.value = MyPageUiState()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _isLoggedIn.value = false
            _uiState.value = MyPageUiState()
        }
    }
}
