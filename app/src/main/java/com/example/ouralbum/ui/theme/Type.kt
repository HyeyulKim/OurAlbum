package com.example.ouralbum.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.ouralbum.R

// 폰트 설정
val TitleFont = FontFamily(
    Font(R.font.pacifico_regular, FontWeight.Normal)
)

val BodyFont = FontFamily(
    Font(R.font.pretendard_extralight, FontWeight.Normal)
)

val BodyFontBold = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Normal)
)

// Typography 정의
val OurTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = TitleFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
val Typography.bodyLargeBold: TextStyle
    get() = this.bodyLarge.copy(fontFamily = BodyFontBold)
