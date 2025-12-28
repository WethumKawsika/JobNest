package com.example.jobnest.main.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun JobListItem(
    job: Job,
    onBookmarkClick: (Job) -> Unit,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onBookmarkClick(job.copy(isBookmarked = !job.isBookmarked)) }) {
                    Icon(
                        imageVector = if (job.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark"
                    )
                }
            }
            Text(text = "💰 ${job.salary}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "${job.company} • ${job.location}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "⏰ ${job.workTime}", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = onCallClick) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}