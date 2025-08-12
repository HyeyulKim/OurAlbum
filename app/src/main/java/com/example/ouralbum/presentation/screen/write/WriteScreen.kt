package com.example.ouralbum.presentation.screen.write

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.ui.util.Dimension

@Composable
fun WriteScreen(
    viewModel: WriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val sectionSpacing = Dimension.scaledHeight(0.015f)
    val bottomBarHeight = Dimension.scaledHeight(0.1f)

    val contentFontSize = Dimension.scaledFont(0.02f)
    val buttonFontSize = Dimension.scaledFont(0.025f)

    val horizontalPadding = Dimension.paddingSmall()

    // 이미지 선택기
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.onImageSelected(uri)
        }
    )

    // 업로드 결과에 따라 스낵바 표시
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is WriteViewModel.UploadState.Success -> {
                snackbarHostState.showSnackbar("업로드 완료!")
                viewModel.resetForm() // uploadState = Idle
            }
            is WriteViewModel.UploadState.Failure -> {
                val msg = (uploadState as WriteViewModel.UploadState.Failure).message
                snackbarHostState.showSnackbar("업로드 실패: $msg")
                viewModel.setUploadIdle() // 실패 후에도 다시 버튼 누를 수 있도록
            }
            else -> Unit
        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "New Post"
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight)
                    .padding(vertical = sectionSpacing, horizontal = horizontalPadding),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.submitWrite() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = uploadState is WriteViewModel.UploadState.Idle
                ) {
                    if (uploadState is WriteViewModel.UploadState.Loading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }else {
                        Text("게시하기", fontSize = buttonFontSize)
                    }
                }
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = horizontalPadding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            Spacer(modifier = Modifier.height(Dimension.scaledHeight(0.0f)))

            // 제목 입력
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = { Text("제목을 적어주세요", fontSize = contentFontSize) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = contentFontSize
                ),
                singleLine = true
            )

            // 본문 입력
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.onContentChange(it) },
                placeholder = { Text("사진 속 함께 했던 추억을 적어주세요.", fontSize = contentFontSize) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = contentFontSize),
                singleLine = false,
                maxLines = 5
            )

            // 이미지 업로드 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUri != null) {
                    AsyncImage(
                        model = uiState.imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Add Photo", tint = Color.Gray)
                }
            }
        }
    }
}
