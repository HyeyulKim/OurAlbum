package com.example.ouralbum.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.ui.theme.bodyLargeBold

@Composable
fun PhotoCard(
    photo: Photo,
    onBookmarkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = photo.title,
                style = MaterialTheme.typography.bodyLargeBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = photo.date,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (photo.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "북마크"
                )
            }
        }

        AsyncImage(
            model = photo.imageUrl,
            contentDescription = "사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
    }
}
