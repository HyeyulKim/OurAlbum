@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ouralbum.presentation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.ErrorView
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun PhotoDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val commentUi by viewModel.commentUiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black
    val containerColor = if (isDark) Color.Black else Color.White

    val isBookmarked = ui.photo?.isBookmarked == true

    LaunchedEffect(commentUi.opened) {
        if (commentUi.opened) {
            // 댓글창 열리면 입력창에 포커스 -> 키패드 올라옴
            focusRequester.requestFocus()
        }
    }

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
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier.background(containerColor)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = {
                                        menuExpanded = false
                                        onEdit(ui.photo!!.id)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()) {
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
                        // 사진
                        item {
                            AsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.title,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        // 댓글 버튼
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { viewModel.toggleComments(p.id) },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.heightIn(min = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = "댓글",
                                        tint = contentColor
                                    )
                                    if (commentUi.count > 0) {
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            text = "${commentUi.count}",
                                            color = contentColor
                                        )
                                    }
                                }

                                Spacer(Modifier.weight(1f))

                                // 북마크
                                IconToggleButton(
                                    checked = isBookmarked,
                                    onCheckedChange = { viewModel.onBookmarkClick(p.id) },
                                    modifier = Modifier.heightIn(min = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = if (isBookmarked) "북마크 해제" else "북마크",
                                        tint = contentColor
                                    )
                                }
                            }
                        }
                        // 본문
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .padding(top = 0.dp)
                            ) {
                                Text(
                                    if (p.content.isBlank()) "내용 없음" else p.content,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(4.dp))
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
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("게시물을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.delete(onDeleted = onBack)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("삭제") }
                    },
                    containerColor = containerColor,
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = contentColor
                            )
                        ) { Text("취소") }
                    }
                )
            }
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (commentUi.opened) {
        ModalBottomSheet(
            onDismissRequest = { ui.photo?.id?.let(viewModel::toggleComments) },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = containerColor,
            tonalElevation = 0.dp
        ) {
            // 리스트
            if (commentUi.loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 2.dp)
            ) {
                items(commentUi.comments, key = { it.id }) { c ->
                    var menuExpanded by remember { mutableStateOf(false) }
                    var showEditDialog by remember { mutableStateOf(false) }
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    var editText by remember(c.id, c.text) { mutableStateOf(c.text) }
                    val isMine = viewModel.myUid != null && viewModel.myUid == c.userId

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = if ((c.authorName
                                        ?: "").isBlank()
                                ) "알 수 없음" else c.authorName!!,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = c.text, style = MaterialTheme.typography.bodyMedium)
                            if (c.createdAtText.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = c.createdAtText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (isMine) {
                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Rounded.MoreVert, contentDescription = "댓글 메뉴")
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    modifier = Modifier.background(containerColor)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("수정") },
                                        onClick = { menuExpanded = false; showEditDialog = true }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "삭제",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = { menuExpanded = false; showDeleteDialog = true }
                                    )
                                }
                            }
                        }
                    }

                    if (showEditDialog) {
                        AlertDialog(
                            onDismissRequest = { showEditDialog = false },
                            title = { Text("댓글 수정") },
                            text = {
                                OutlinedTextField(
                                    value = editText,
                                    onValueChange = { editText = it })
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showEditDialog = false
                                    val newText = editText.trim()
                                    if (newText.isNotEmpty()) viewModel.updateComment(c.id, newText)
                                }) { Text("저장") }
                            },
                            containerColor = containerColor,
                            dismissButton = {
                                TextButton(
                                    onClick = { showEditDialog = false },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = contentColor
                                    )
                                ) { Text("취소") }
                            }
                        )
                    }
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("댓글을 삭제하시겠습니까?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteDialog = false
                                        viewModel.deleteComment(c.id)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) { Text("삭제") }
                            },
                            containerColor = containerColor,
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteDialog = false },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = contentColor
                                    )
                                ) { Text("취소") }
                            }
                        )
                    }
                }
            }

            // 입력창
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    value = commentUi.input,
                    onValueChange = viewModel::onCommentInputChange,
                    placeholder = { Text("댓글을 입력하세요") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = commentUi.input.isNotBlank() && !commentUi.sending,
                    onClick = { viewModel.sendComment() }
                ) {
                    if (commentUi.sending) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("전송")
                    }
                }
            }
        }
    }
}

