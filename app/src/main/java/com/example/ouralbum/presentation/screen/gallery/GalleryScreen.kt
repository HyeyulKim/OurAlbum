package com.example.ouralbum.presentation.screen.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ouralbum.presentation.component.AppDivider
import com.example.ouralbum.presentation.component.PhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Our Album",
                            style = MaterialTheme.typography.displaySmall,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.height(48.dp),
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                AppDivider()
            }

        }
    ) { paddingValues ->
        if (isLoggedIn) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(uiState.photos) { photo ->
                    PhotoCard(
                        photo = photo,
                        onBookmarkClick = { viewModel.onBookmarkClick(photo.id) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인이 필요합니다",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
