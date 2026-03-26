package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun CategoriesSection() {

    Column {

        SectionHeader(
            title = "Categories"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

            val categories = listOf(
                "Fiction",
                "Sci-Fi",
                "Fantasy",
                "Business",
                "History"
            )

            items(categories) { category ->

                AssistChip(
                    onClick = {},
                    label = { Text(category) }
                )

            }

        }

    }

}