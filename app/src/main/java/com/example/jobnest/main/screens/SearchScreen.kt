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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onSwitchView: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var salaryRange by remember { mutableStateOf(500f..3000f) }
    var workTypeExpanded by remember { mutableStateOf(false) }
    var selectedWorkType by remember { mutableStateOf("Select work type") }
    var locationExpanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("Select location") }
    var genderExpanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select preference") }
    var selectedWorkTime by remember { mutableStateOf("") }

    val workTypes = listOf("Food Service", "Delivery", "Office Work", "Retail")
    val locations = listOf("Colombo", "Galle", "Kandy", "Jaffna")
    val genders = listOf("Male", "Female", "Any")
    val workTimes = listOf("Morning", "Afternoon", "Evening", "Night")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(text = "Search & Filter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search jobs...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { /* TODO: Clear filters */ }) {
                        Text("Clear All")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Salary Range (Rs./day)", fontWeight = FontWeight.Medium)
                RangeSlider(
                    value = salaryRange,
                    onValueChange = { salaryRange = it },
                    valueRange = 0f..5000f,
                    steps = 100
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rs. ${salaryRange.start.toInt()}")
                    Text("Rs. ${salaryRange.endInclusive.toInt()}")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Work Type Dropdown
                ExposedDropdownMenuBox(expanded = workTypeExpanded, onExpandedChange = { workTypeExpanded = !workTypeExpanded }) {
                    OutlinedTextField(
                        value = selectedWorkType,
                        onValueChange = {}, // readOnly
                        readOnly = true,
                        label = { Text("Work Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = workTypeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = workTypeExpanded, onDismissRequest = { workTypeExpanded = false }) {
                        workTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { selectedWorkType = type; workTypeExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Location Dropdown
                ExposedDropdownMenuBox(expanded = locationExpanded, onExpandedChange = { locationExpanded = !locationExpanded }) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Location") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = locationExpanded, onDismissRequest = { locationExpanded = false }) {
                        locations.forEach { location ->
                            DropdownMenuItem(text = { Text(location) }, onClick = { selectedLocation = location; locationExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Gender Preference Dropdown
                ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender Preference") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                        genders.forEach { gender ->
                            DropdownMenuItem(text = { Text(gender) }, onClick = { selectedGender = gender; genderExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Work Time", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    workTimes.forEach { time ->
                        val isSelected = selectedWorkTime == time
                        Button(
                            onClick = { selectedWorkTime = time },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(time)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}
