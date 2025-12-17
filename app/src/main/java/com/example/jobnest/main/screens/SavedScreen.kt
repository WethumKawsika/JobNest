package com.example.jobnest.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobnest.main.screens.common.Job
import com.example.jobnest.main.screens.common.JobListItem

@Composable
fun SavedScreen(onSwitchView: () -> Unit = {}) {
    val savedJobs = remember {
        mutableStateListOf(
            Job("Delivery Driver", "Rs. 1200/day", "Galle", "Delivery", "Delivery driver needed for morning shifts. Must have own vehicle.", isSaved = true)
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Saved Jobs", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                TextButton(onClick = onSwitchView) {
                    Text("Switch to Owner", color = Color.White)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "You have ${savedJobs.size} saved job${if (savedJobs.size != 1) "s" else ""}")
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(savedJobs) { job ->
                        JobListItem(job = job, onBookmarkClick = {
                            if (it.isSaved) {
                                if (!savedJobs.contains(it)) {
                                    savedJobs.add(it)
                                }
                            } else {
                                savedJobs.removeIf { j -> j.title == it.title } // simple removal for demo
                            }
                        })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenPreview() {
    SavedScreen()
}
