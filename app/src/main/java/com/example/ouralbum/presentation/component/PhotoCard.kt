package com.example.ouralbum.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ouralbum.domain.model.Photo
import com.example.ouralbum.ui.util.Dimension

@Composable
fun PhotoCard(
    photo: Photo,
    bookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier.fillMaxWidth(),
    imageContentScale: ContentScale = ContentScale.FillWidth,
    showDate: Boolean = true
) {
    val fontSizeTitle = Dimension.scaledFont(0.02f)
    val fontSizeContent = Dimension.scaledFont(0.018f)
    val fontSizeMeta = Dimension.scaledFont(0.016f)   // 닉네임(윗줄)
    val fontSizeDate = Dimension.scaledFont(0.017f)
    val iconSize = Dimension.scaledWidth(0.06f)
    val paddingHorizontal = Dimension.paddingSmall()
    val paddingVertical = Dimension.scaledHeight(0.01f)

    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    val avatarSize = 40.dp
    val avatarBg = MaterialTheme.colorScheme.surfaceVariant

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
            // 왼쪽: 큰 원형 아바타
            val authorPhotoUrl = photo.authorPhotoUrl
            if (!authorPhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(authorPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "작성자 프로필",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(avatarBg)
                )
            }

            Spacer(Modifier.width(10.dp))

            // 가운데: 닉네임(위) + 제목(아래)
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                val name = photo.authorName.orEmpty()
                Text(
                    text = if (name.isNotBlank()) name else "알 수 없음",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSizeMeta),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeTitle),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // 오른쪽: (윗줄) 날짜 + 북마크 아이콘을 가로로 나란히
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showDate) {
                    Text(
                        text = photo.date,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeDate),
                        maxLines = 1
                    )
                    Spacer(Modifier.width(6.dp))
                }
                IconToggleButton(
                    checked = bookmarked,
                    onCheckedChange = { onBookmarkClick() },
                    modifier = Modifier.size(iconSize)
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (bookmarked) "북마크됨" else "북마크",
                        tint = contentColor
                    )
                }
            }
        }

        // 본문 이미지
        AsyncImage(
            model = photo.imageUrl,
            contentDescription = "사진: ${photo.title}",
            contentScale = imageContentScale,
            modifier = imageModifier
        )

        // 내용 요약
        Text(
            text = if (photo.content.isBlank()) "내용 없음" else photo.content,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSizeContent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingHorizontal, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
