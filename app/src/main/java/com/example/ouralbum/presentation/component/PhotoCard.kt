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
import coil.compose.AsyncImage
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.ui.theme.bodyLargeBold
import com.example.ouralbum.ui.util.Dimension

@Composable
fun PhotoCard(
    photo: Photo,
    onBookmarkClick: () -> Unit
) {
    val fontSizeTitle = Dimension.scaledFont(0.02f)     // 제목 글씨 크기
    val fontSizeDate = Dimension.scaledFont(0.018f)     // 날짜 글씨 크기
    val iconSize = Dimension.scaledWidth(0.06f)         // 아이콘 크기 (화면 너비 기준)
    val paddingHorizontal = Dimension.paddingSmall()
    val paddingVertical = Dimension.scaledHeight(0.01f)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = photo.title,
                style = MaterialTheme.typography.bodyLargeBold.copy(fontSize = fontSizeTitle),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = photo.date,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeDate),
                modifier = Modifier.padding(end = paddingHorizontal)
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
            contentDescription = "사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 정사각형 비율 유지
        )
    }
}
