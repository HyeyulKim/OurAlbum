package com.example.ouralbum.presentation.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.ErrorView

@Composable
fun PhotoEditScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: PhotoEditViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ui.done) {
        if (ui.done) onDone()
    }

    Scaffold(topBar = { AppTopBar(title = "게시물 수정", onBack = onBack) }) { padding ->
        when {
            ui.isLoading -> Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            ui.error != null -> ErrorView(
                message = ui.error ?: "오류",
                onRetry = { /* 필요 시 화면 이탈 후 재진입으로 로드, 여기선 생략 */ }
            )
            else -> {
                Column(
                    Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ui.title,
                        onValueChange = viewModel::onTitleChange,
                        label = { Text("제목") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ui.content,
                        onValueChange = viewModel::onContentChange,
                        label = { Text("내용") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5
                    )
                    Button(
                        onClick = { viewModel.submit() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("저장") }
                }
            }
        }
    }
}
