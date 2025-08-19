package com.example.ouralbum.presentation.screen.write

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.data.remote.PhotoUploader
import com.example.ouralbum.domain.auth.AuthStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val photoUploader: PhotoUploader,
    authStateProvider: AuthStateProvider
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = authStateProvider.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun onContentChange(newContent: String) {
        _uiState.value = _uiState.value.copy(content = newContent)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri) 
    }


    fun submitWrite() {
        if (_uploadState.value == UploadState.Loading) return // 중복 방지

        val title = _uiState.value.title
        val imageUri = _uiState.value.imageUri
        val content = _uiState.value.content

        if (imageUri == null || title.isBlank()) {
            _uploadState.value = UploadState.Failure("제목 또는 이미지가 없습니다.")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            photoUploader.uploadPhoto(
                imageUri = imageUri,
                title = title,
                content = content,
                onSuccess = {
                    _uploadState.value = UploadState.Success
                },
                onFailure = {
                    _uploadState.value = UploadState.Failure(it.message ?: "업로드 실패")
                }
            )
        }
    }

    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        object Success : UploadState()
        data class Failure(val message: String) : UploadState()
    }

    fun resetForm() {
        _uiState.value = WriteUiState()
        _uploadState.value = UploadState.Idle
    }

    fun setUploadIdle() {
        _uploadState.value = UploadState.Idle
    }

}
