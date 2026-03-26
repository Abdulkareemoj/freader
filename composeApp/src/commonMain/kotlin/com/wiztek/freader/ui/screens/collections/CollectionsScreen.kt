package com.wiztek.freader.ui.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Shared", "Device Only")

    val collections = listOf(
        CollectionGridItem("Favorites", "128 books", "2d ago", Color(0xFFFFEBDD)),
        CollectionGridItem("To Read", "45 books", "1w ago", Color(0xFFE2E9F0)),
        CollectionGridItem("Sci-Fi", "32 books", "3d ago", Color(0xFFE2E9F0)),
        CollectionGridItem("History", "18 books", "5d ago", Color(0xFFE2E9F0)),
        CollectionGridItem("Art & Design", "12 books", "1mo ago", Color(0xFFE2E9F0)),
        CollectionGridItem("Travel", "7 books", "3mo ago", Color(0xFFE2E9F0))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFFF05A28), // Orange indicator/text color
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFFF05A28) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(collections) { item ->
                    CollectionGridCard(item)
                }
            }

            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Color(0xFFF05A28),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Create new collection")
            }
        }
    }
}

data class CollectionGridItem(
    val title: String,
    val count: String,
    val time: String,
    val color: Color
)

@Composable
fun CollectionGridCard(item: CollectionGridItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(item.color)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Bookmark, null, modifier = Modifier.size(16.dp), tint = Color.Black.copy(alpha = 0.3f))
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(16.dp), tint = Color.Black.copy(alpha = 0.3f))
                    }
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(16.dp), tint = Color.Black.copy(alpha = 0.3f))
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        if (item.title == "To Read") {
                            Text("+12", style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.3f))
                        } else {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(16.dp), tint = Color.Black.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("${item.count} • ${item.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
