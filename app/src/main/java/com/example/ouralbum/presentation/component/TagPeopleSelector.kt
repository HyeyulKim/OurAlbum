package com.example.ouralbum.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ouralbum.ui.util.Dimension

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagPeopleSelector(
    allPeople: List<String>,
    selectedPeople: List<String>,
    onSelectionChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    // 선택된 사람이 바뀌면 검색어 초기화
    LaunchedEffect(selectedPeople) {
        query = ""
    }

    val filtered = allPeople.filter {
        it.contains(query, ignoreCase = true) && it !in selectedPeople
    }

    val spacingSmall = Dimension.scaledWidth(0.02f)
    val spacingTiny = Dimension.scaledWidth(0.01f)
    val textFontSize = Dimension.scaledFont(0.018f)
    val chipFontSize = Dimension.scaledFont(0.016f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacingSmall)
    ) {
        // 검색창
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text("함께한 사람을 검색하세요", fontSize = textFontSize)
            },
            textStyle = LocalTextStyle.current.copy(fontSize = textFontSize),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // 추천 인물: 선택 전 상태 (outlined 스타일)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingTiny)
        ) {
            filtered.take(5).forEach { person ->
                AssistChip(
                    onClick = {
                        if (person !in selectedPeople) {
                            onSelectionChange(selectedPeople + person)
                        }
                    },
                    label = { Text(person, fontSize = chipFontSize) },
                    // 선택 전: 기본 outlined 스타일
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // 선택된 인물: filled 스타일
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingTiny)
        ) {
            selectedPeople.forEach { person ->
                AssistChip(
                    onClick = {
                        // 클릭 시 선택 해제 (옵션)
                        onSelectionChange(selectedPeople - person)
                    },
                    label = { Text(person, fontSize = chipFontSize) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}
