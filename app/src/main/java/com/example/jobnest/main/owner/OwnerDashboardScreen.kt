package com.example.jobnest.main.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OwnerDashboardScreen(
    onPostJobClick: () -> Unit,
    onSwitchView: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Owner Dashboard")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPostJobClick) {
            Text("Post a New Job")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSwitchView) {
            Text("Switch to Student View")
        }
    }
}
