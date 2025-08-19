package com.example.ouralbum.presentation.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.ErrorView

@Composable
fun PhotoDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = ui.photo?.title ?: "상세",
                onBack = onBack
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {

            when {
                ui.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                ui.error != null -> ErrorView(
                    message = ui.error ?: "오류",
                    onRetry = { viewModel.reload() }
                )

                ui.photo != null -> {
                    val p = ui.photo!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .then(Modifier)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        item {
                            Text(p.title, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(p.date, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                        item {
                            Divider()
                            Text(
                                if (p.content.isBlank()) "내용 없음" else p.content,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (ui.isOwner) {
                            item {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onEdit(p.id) }) { Text("수정") }
                                    OutlinedButton(
                                        onClick = { showDeleteDialog = true },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) { Text("삭제") }
                                }
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("삭제하시겠어요?") },
                    text = { Text("이미지와 게시물이 모두 삭제됩니다. 되돌릴 수 없어요.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            viewModel.delete(onDeleted = onBack)
                        }) { Text("삭제") }
                    },
                    dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("취소") } }
                )
            }
        }
    }
}
