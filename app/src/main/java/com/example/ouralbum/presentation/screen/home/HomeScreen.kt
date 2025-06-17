package com.example.ouralbum.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.presentation.component.AppDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            itemsIndexed(uiState.photos) { index, photo ->
                PhotoCard(
                    photo = photo,
                    onBookmarkClick = { viewModel.onBookmarkClick(photo.id) }
                )

                if (index < uiState.photos.lastIndex) {
                    AppDivider()
                }
            }
        }

    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    onBookmarkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = photo.title,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (photo.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "북마크"
                )
            }
        }

        AsyncImage(
            model = photo.imageUrl,
            contentDescription = "사진",
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = photo.date, style = MaterialTheme.typography.bodySmall)
        }
    }
}

