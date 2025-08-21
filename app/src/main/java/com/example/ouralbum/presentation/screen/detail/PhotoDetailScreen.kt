package com.example.ouralbum.presentation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = ui.photo?.title ?: "상세",
                onBack = onBack,
                actions = {
                    // 본인 글일 때만 점세개 메뉴 노출
                    if (ui.isOwner && ui.photo != null) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Rounded.MoreVert, contentDescription = "메뉴")
                            }
                            val isDark = isSystemInDarkTheme()

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier.background(if (isDark) Color.Black else Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = {
                                        menuExpanded = false
                                        onEdit(ui.photo!!.id)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteDialog = true
                                    },
                                )
                            }
                        }
                    }
                }
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            AsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.title,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    if (p.content.isBlank()) "내용 없음" else p.content,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    p.date,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                val isDark = isSystemInDarkTheme()
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("게시물을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            viewModel.delete(onDeleted = onBack)
                        }) { Text("삭제") }
                    },
                    containerColor = if (isDark) Color.Black else Color.White,
                    dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }}
                )
            }
        }
    }
}
