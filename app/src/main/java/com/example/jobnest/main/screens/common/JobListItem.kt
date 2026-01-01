package com.example.jobnest.main.screens.common
import androidx.compose.foundation.BorderStroke

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modern Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentGreen = Color(0xFF10B981)
private val BackgroundGray = Color(0xFFF8F9FA)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val CardBackground = Color(0xFFFFFFFF)
private val DividerColor = Color(0xFFE5E7EB)

@Composable
fun JobListItem(
    job: JobUI,
    onBookmarkClick: () -> Unit,
    onCallClick: () -> Unit,
    onLocationClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.08f)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Section with Title, Company & Bookmark (+ optional delete)
            JobHeader(
                job = job,
                onBookmarkClick = onBookmarkClick,
                onDelete = onDelete
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Highlighted Salary Box
            SalaryHighlightBox(salary = job.salary)

            Spacer(modifier = Modifier.height(12.dp))

            // Location with Navigation
            LocationSection(
                location = job.location,
                hasLocation = job.locationLatLng != null,
                onLocationClick = onLocationClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Work Type Tags
            WorkTypeTags(
                workType = job.workType,
                workTime = job.workTime,
                food = job.food,
                transport = job.transport
            )

            // Expandable Details Section
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = DividerColor
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExpandedDetailsSection(job = job)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            ActionButtons(
                job = job,
                expanded = expanded,
                onCallClick = onCallClick,
                onLocationClick = onLocationClick,
                onExpandToggle = { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun JobHeader(
    job: JobUI,
    onBookmarkClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    PrimaryBlue.copy(alpha = 0.15f),
                                    LightBlue.copy(alpha = 0.15f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = job.company,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = TextSecondary
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Bookmark Button
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (job.isBookmarked)
                    PrimaryBlue.copy(alpha = 0.12f)
                else
                    Color(0xFFF3F4F6),
                onClick = onBookmarkClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (job.isBookmarked)
                            Icons.Default.Bookmark
                        else
                            Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (job.isBookmarked) PrimaryBlue else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Optional Delete Button (visible for owners)
            if (onDelete != null) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color(0xFFFFF1F2),
                    onClick = { onDelete() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryHighlightBox(salary: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AccentGreen.copy(alpha = 0.12f),
                        AccentGreen.copy(alpha = 0.06f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AccentGreen.copy(alpha = 0.3f),
                        AccentGreen.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentGreen, Color(0xFF059669))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = "Salary Range",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp
                    ),
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = salary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = AccentGreen
                )
            }
        }
    }
}

@Composable
private fun LocationSection(
    location: String,
    hasLocation: Boolean,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }

        if (hasLocation) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AccentGreen.copy(alpha = 0.12f),
                onClick = onLocationClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Directions,
                        contentDescription = "Get Directions",
                        tint = AccentGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkTypeTags(
    workType: String,
    workTime: String,
    food: String,
    transport: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (workType.isNotBlank()) {
                EnhancedTag(
                    text = workType,
                    icon = Icons.Default.Work,
                    backgroundColor = PrimaryBlue.copy(alpha = 0.1f),
                    textColor = PrimaryBlue
                )
            }

            if (workTime.isNotBlank()) {
                EnhancedTag(
                    text = workTime,
                    icon = Icons.Default.AccessTime,
                    backgroundColor = Color(0xFFFEF3C7),
                    textColor = Color(0xFFD97706)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (food.isNotBlank()) {
                EnhancedTag(
                    text = food,
                    icon = Icons.Default.Restaurant,
                    backgroundColor = Color(0xFFDCFCE7),
                    textColor = Color(0xFF059669)
                )
            }

            if (transport.isNotBlank()) {
                EnhancedTag(
                    text = transport,
                    icon = Icons.Default.DirectionsBus,
                    backgroundColor = Color(0xFFE0E7FF),
                    textColor = Color(0xFF4F46E5)
                )
            }
        }
    }
}

@Composable
private fun EnhancedTag(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp
                ),
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ExpandedDetailsSection(job: JobUI) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Description
        if (job.description.isNotBlank()) {
            DetailBlock(
                icon = Icons.Default.Description,
                label = "Job Description",
                content = {
                    Text(
                        text = job.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        color = Color(0xFF374151)
                    )
                }
            )
        }

        // Requirements Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoChip(
                icon = Icons.Default.People,
                label = "Needed",
                value = "${job.requiredPersons}",
                modifier = Modifier.weight(1f)
            )

            if (job.ageLimit?.isNotBlank() == true) {
                InfoChip(
                    icon = Icons.Default.CalendarToday,
                    label = "Age Limit",
                    value = job.ageLimit,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Gender Preference
        if (job.genderPreference.isNotBlank()) {
            DetailBlock(
                icon = Icons.Default.Group,
                label = "Gender Preference",
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoChip(
                            icon = Icons.Default.PersonOutline,
                            label = "Preference",
                            value = job.genderPreference
                        )

                        if (job.genderPreference == "Both") {
                            job.boysCount?.let { boys ->
                                InfoChip(
                                    icon = Icons.Default.Male,
                                    label = "Boys",
                                    value = "$boys"
                                )
                            }
                            job.girlsCount?.let { girls ->
                                InfoChip(
                                    icon = Icons.Default.Female,
                                    label = "Girls",
                                    value = "$girls"
                                )
                            }
                        }
                    }
                }
            )
        }

        // Contact Information
        if (job.contactNumber.isNotBlank()) {
            DetailBlock(
                icon = Icons.Default.Phone,
                label = "Contact Number",
                content = {
                    Text(
                        text = job.contactNumber,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = PrimaryBlue
                    )
                }
            )
        }
    }
}

@Composable
private fun DetailBlock(
    icon: ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = TextPrimary
            )
        }
        content()
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = BackgroundGray,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = TextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    job: JobUI,
    expanded: Boolean,
    onCallClick: () -> Unit,
    onLocationClick: () -> Unit,
    onExpandToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Call Button
            GradientButton(
                text = "Call Now",
                icon = Icons.Default.Phone,
                gradientColors = listOf(PrimaryBlue, LightBlue),
                onClick = onCallClick,
                modifier = Modifier.weight(1f)
            )

            // Directions Button
            if (job.locationLatLng != null) {
                GradientButton(
                    text = "Directions",
                    icon = Icons.Default.Directions,
                    gradientColors = listOf(AccentGreen, Color(0xFF059669)),
                    onClick = onLocationClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expand/Collapse Button
        TextButton(
            onClick = onExpandToggle,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (expanded) "Show Less" else "Show More Details",
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Icon(
                imageVector = if (expanded)
                    Icons.Default.ExpandLess
                else
                    Icons.Default.ExpandMore,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun GradientButton(
    text: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(colors = gradientColors)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}