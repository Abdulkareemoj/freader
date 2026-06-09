package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wiztek.freader.reader.model.BookFormat

data class CategoryChip(
    val label: String,
    val format: BookFormat? = null,
    val icon: ImageVector? = null
)

@Composable
fun CategoriesSection(
    categories: List<CategoryChip>,
    onCategoryClick: (CategoryChip) -> Unit = {}
) {
    Column {
        SectionHeader(title = "Categories")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories, key = { it.label }) { category ->
                AssistChip(
                    onClick = { onCategoryClick(category) },
                    label = { Text(category.label, style = MaterialTheme.typography.labelLarge) },
                    leadingIcon = if (category.icon != null) {
                        { Icon(category.icon, contentDescription = null, modifier = Modifier) }
                    } else {
                        null
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
    }
}
