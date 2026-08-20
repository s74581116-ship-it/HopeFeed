package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiMatchingEngine
import com.example.data.model.DonationStatus
import com.example.data.model.DonorType
import com.example.data.model.FoodDonationEntity
import com.example.data.model.ShelterEntity
import com.example.ui.components.DonorTypeBadge
import com.example.ui.components.StatusBadge
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun MapScreen(
    donations: List<FoodDonationEntity>,
    shelters: List<ShelterEntity>,
    onDonationClick: (FoodDonationEntity) -> Unit,
    onAiMatchClick: (FoodDonationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDonationPin by remember { mutableStateOf<FoodDonationEntity?>(donations.firstOrNull()) }
    var selectedShelterPin by remember { mutableStateOf<ShelterEntity?>(null) }
    var showSheltersOnly by remember { mutableStateOf(false) }
    var showActiveRoutes by remember { mutableStateOf(true) }

    // Bounding box for mapping coordinates to canvas coordinates (SF downtown cluster)
    val minLat = 37.7500
    val maxLat = 37.8100
    val minLon = -122.4500
    val maxLon = -122.4000

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_screen_container")
    ) {
        // Map Radar Canvas
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val canvasHeight = constraints.maxHeight.toFloat()

            fun project(lat: Double, lon: Double): Offset {
                val normX = ((lon - minLon) / (maxLon - minLon)).coerceIn(0.1, 0.9).toFloat()
                val normY = (1.0f - ((lat - minLat) / (maxLat - minLat)).coerceIn(0.1, 0.9)).toFloat()
                return Offset(normX * canvasWidth, normY * canvasHeight * 0.78f + 60f)
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(donations, shelters) {
                        detectTapGestures { tapOffset ->
                            var foundDonation: FoodDonationEntity? = null
                            for (donation in donations) {
                                val pt = project(donation.latitude, donation.longitude)
                                val dist = sqrt((pt.x - tapOffset.x) * (pt.x - tapOffset.x) + (pt.y - tapOffset.y) * (pt.y - tapOffset.y))
                                if (dist < 80f) {
                                    foundDonation = donation
                                    break
                                }
                            }
                            if (foundDonation != null) {
                                selectedDonationPin = foundDonation
                                selectedShelterPin = null
                            } else {
                                for (shelter in shelters) {
                                    val pt = project(shelter.latitude, shelter.longitude)
                                    val dist = sqrt((pt.x - tapOffset.x) * (pt.x - tapOffset.x) + (pt.y - tapOffset.y) * (pt.y - tapOffset.y))
                                    if (dist < 80f) {
                                        selectedShelterPin = shelter
                                        selectedDonationPin = null
                                        break
                                    }
                                }
                            }
                        }
                    }
            ) {
                // Background map styled grid
                drawRect(color = Color(0xFFF1F5F2))

                // Simulated city road network grid
                val gridSpacing = 90f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0xFFE2EBE4),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 3f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0xFFE2EBE4),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 3f
                    )
                    y += gridSpacing
                }

                // Major Arterial Road diagonal
                drawLine(
                    color = Color(0xFFD4E3D8),
                    start = Offset(0f, size.height * 0.8f),
                    end = Offset(size.width, size.height * 0.2f),
                    strokeWidth = 10f
                )
                drawLine(
                    color = Color(0xFFD4E3D8),
                    start = Offset(size.width * 0.2f, 0f),
                    end = Offset(size.width * 0.8f, size.height),
                    strokeWidth = 8f
                )

                // Draw Connecting Logistics Routes for claimed/in-transit donations
                if (showActiveRoutes) {
                    donations.filter { it.status == DonationStatus.CLAIMED || it.status == DonationStatus.IN_TRANSIT }.forEach { d ->
                        val donorPt = project(d.latitude, d.longitude)
                        val targetShelter = shelters.firstOrNull { it.id == d.claimedShelterId } ?: shelters.firstOrNull()
                        if (targetShelter != null) {
                            val shelterPt = project(targetShelter.latitude, targetShelter.longitude)

                            // Route Path
                            drawLine(
                                color = Color(0xFF0D9488),
                                start = donorPt,
                                end = shelterPt,
                                strokeWidth = 5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                            )

                            // Courier Position mid-way
                            val courierPt = Offset(
                                (donorPt.x + shelterPt.x) / 2f,
                                (donorPt.y + shelterPt.y) / 2f
                            )
                            drawCircle(
                                color = Color(0xFF0D9488),
                                radius = 18f,
                                center = courierPt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 10f,
                                center = courierPt
                            )
                        }
                    }
                }

                // Draw Shelter Pins (Blue/Purple squares with halo)
                shelters.forEach { shelter ->
                    val pt = project(shelter.latitude, shelter.longitude)
                    val isSelected = selectedShelterPin?.id == shelter.id

                    // Halo
                    drawCircle(
                        color = if (isSelected) Color(0x663B82F6) else Color(0x333B82F6),
                        radius = if (isSelected) 32f else 22f,
                        center = pt
                    )
                    // Shelter Core
                    drawCircle(
                        color = Color(0xFF2563EB),
                        radius = if (isSelected) 18f else 14f,
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = pt
                    )
                }

                // Draw Donor Surplus Pins
                if (!showSheltersOnly) {
                    donations.forEach { donation ->
                        val pt = project(donation.latitude, donation.longitude)
                        val isSelected = selectedDonationPin?.id == donation.id

                        val pinColor = when (donation.donorType) {
                            DonorType.RESTAURANT -> Color(0xFFD97706)
                            DonorType.CELEBRATION_EVENT -> Color(0xFFDB2777)
                            DonorType.BAKERY -> Color(0xFFB45309)
                            DonorType.GROCERY -> Color(0xFF0E8345)
                            else -> Color(0xFF6B21A8)
                        }

                        // Outer pulse ring
                        drawCircle(
                            color = pinColor.copy(alpha = if (isSelected) 0.45f else 0.25f),
                            radius = if (isSelected) 36f else 24f,
                            center = pt
                        )
                        // Inner pin
                        drawCircle(
                            color = pinColor,
                            radius = if (isSelected) 20f else 15f,
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 6f,
                            center = pt
                        )
                    }
                }
            }
        }

        // Top Control Card & Filter Chips
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "Live Surplus & Shelter Radar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = showActiveRoutes,
                            onClick = { showActiveRoutes = !showActiveRoutes },
                            label = { Text("Routes", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }

            // Legend Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LegendPill("🍽️ Restaurant", Color(0xFFD97706))
                LegendPill("🎉 Event Banquet", Color(0xFFDB2777))
                LegendPill("🥖 Bakery", Color(0xFFB45309))
                LegendPill("🏠 Shelter", Color(0xFF2563EB))
            }
        }

        // Bottom Pin Detail Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
        ) {
            if (selectedDonationPin != null) {
                val donation = selectedDonationPin!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_pin_detail_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DonorTypeBadge(donorType = donation.donorType)
                            StatusBadge(status = donation.status)
                        }

                        Text(
                            text = donation.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "📍 ${donation.pickupAddress} (${donation.servings} Servings / ~${donation.weightKg}kg)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onDonationClick(donation) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Details & Pickup", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { onAiMatchClick(donation) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("AI Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (selectedShelterPin != null) {
                val shelter = selectedShelterPin!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "PARTNER SHELTER",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "Capacity: ${shelter.capacity} people",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = shelter.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "📍 ${shelter.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Current Need: ${shelter.urgentNeedNote}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendPill(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
