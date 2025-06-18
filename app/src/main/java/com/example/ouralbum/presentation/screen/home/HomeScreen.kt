package com.example.ouralbum.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ouralbum.presentation.component.AppDivider
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.PhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Our Album"
            )
        }
    ) { paddingValues ->
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
    }
}

