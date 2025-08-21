package com.example.ouralbum.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.ui.theme.bodyLargeBold
import com.example.ouralbum.ui.util.Dimension

@Composable
fun PhotoCard(
    photo: Photo,
    onBookmarkClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontSizeTitle = Dimension.scaledFont(0.02f)
    val fontSizeDate = Dimension.scaledFont(0.017f)
    val iconSize = Dimension.scaledWidth(0.06f)
    val paddingHorizontal = Dimension.paddingSmall()
    val paddingVertical = Dimension.scaledHeight(0.01f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = photo.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeTitle),
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Text(
                text = photo.date,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeDate),
                modifier = Modifier.padding(end = paddingHorizontal),
                maxLines = 1
            )

            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    imageVector = if (photo.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "북마크"
                )
            }
        }

        AsyncImage(
            model = photo.imageUrl,
            contentDescription = "사진: ${photo.title}",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
