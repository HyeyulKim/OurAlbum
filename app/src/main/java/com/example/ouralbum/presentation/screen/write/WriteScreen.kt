package com.example.ouralbum.presentation.screen.write

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ouralbum.presentation.component.AppDivider
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.TagPeopleSelector
import com.example.ouralbum.ui.util.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(viewModel: WriteViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val sectionSpacing = Dimension.scaledHeight(0.015f)
    val tagSelectorHeight = Dimension.scaledHeight(0.20f)
    val bottomBarHeight = Dimension.scaledHeight(0.1f)

    val contentFontSize = Dimension.scaledFont(0.02f)
    val buttonFontSize = Dimension.scaledFont(0.025f)

    val horizontalPadding = Dimension.paddingSmall()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "New Post"
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight)
                    .padding(vertical = sectionSpacing,horizontal = horizontalPadding),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* 저장 처리 */ },
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("게시하기", fontSize = buttonFontSize)
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = contentFontSize
                ),
                singleLine = true
            )

            // 함께한 사람 선택창
            TagPeopleSelector(
                allPeople = listOf("김철수", "최영희", "강진영", "이지윤", "박영철"),
                selectedPeople = uiState.selectedPeople,
                onSelectionChange = { viewModel.onSelectedPeopleChange(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // 이미지 업로드 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 정사각형 비율
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Photo", tint = Color.Gray)
            }
        }
    }
}
