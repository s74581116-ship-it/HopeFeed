package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ai.DonationDraftResult
import com.example.data.model.DonorType
import com.example.data.model.FoodCategory
import com.example.data.model.StorageRequirement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(
    lastAiDraftResult: DonationDraftResult?,
    isAiDraftingLoading: Boolean,
    onRequestAiDraft: (notes: String, donorType: DonorType) -> Unit,
    onSubmitDonation: (
        title: String,
        description: String,
        donorName: String,
        donorType: DonorType,
        category: FoodCategory,
        servings: Int,
        weightKg: Double,
        dietaryTags: String,
        storageRequirement: StorageRequirement,
        pickupAddress: String,
        contactPhone: String,
        specialInstructions: String,
        eventDetails: String,
        pickupWindow: String,
        expiryHours: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDonorType by remember { mutableStateOf(DonorType.RESTAURANT) }
    var rawAiPromptText by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("Gourmet Dinner Meal Trays") }
    var description by remember { mutableStateOf("Freshly cooked nutritious hot meals prepared today.") }
    var donorName by remember { mutableStateOf("Bella Cucina Restaurant") }
    var category by remember { mutableStateOf(FoodCategory.COOKED_MEALS) }
    var servings by remember { mutableIntStateOf(35) }
    var weightKg by remember { mutableDoubleStateOf(14.0) }
    var dietaryTags by remember { mutableStateOf("Vegetarian, Halal Friendly") }
    var storageRequirement by remember { mutableStateOf(StorageRequirement.HOT_HOLD) }
    var pickupAddress by remember { mutableStateOf("742 Evergreen Terrace, Downtown") }
    var contactPhone by remember { mutableStateOf("+1 (555) 987-6543") }
    var specialInstructions by remember { mutableStateOf("Kitchen back entrance, ask for Chef.") }
    var eventDetails by remember { mutableStateOf("") }
    var pickupWindow by remember { mutableStateOf("Today: Next 2 hours") }
    var expiryHours by remember { mutableIntStateOf(3) }

    // When AI Draft arrives, auto-populate state
    LaunchedEffect(lastAiDraftResult) {
        lastAiDraftResult?.let { draft ->
            title = draft.title
            description = draft.description
            category = draft.category
            servings = draft.servings
            weightKg = draft.weightKg
            dietaryTags = draft.dietaryTags
            storageRequirement = draft.storageRequirement
            expiryHours = draft.expiryHours
            specialInstructions = draft.instructions
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("donate_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Event Banner Header if Celebration Event is chosen
        if (selectedDonorType == DonorType.CELEBRATION_EVENT) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.event_donation_banner_1787235274940),
                        contentDescription = "Event Donation",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Header Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Donate Surplus Food",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Connect with local shelters for quick pickup and save food from going to waste.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Donor Type Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "1. Select Donor Organization Type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DonorTypeOptionCard(
                    donorType = DonorType.RESTAURANT,
                    isSelected = selectedDonorType == DonorType.RESTAURANT,
                    onClick = {
                        selectedDonorType = DonorType.RESTAURANT
                        donorName = "Bella Cucina Restaurant"
                    },
                    modifier = Modifier.weight(1f)
                )
                DonorTypeOptionCard(
                    donorType = DonorType.CELEBRATION_EVENT,
                    isSelected = selectedDonorType == DonorType.CELEBRATION_EVENT,
                    onClick = {
                        selectedDonorType = DonorType.CELEBRATION_EVENT
                        donorName = "Grand Wedding Reception / Party"
                        eventDetails = "Grand Pavilion Ballroom - Wedding Reception"
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DonorTypeOptionCard(
                    donorType = DonorType.BAKERY,
                    isSelected = selectedDonorType == DonorType.BAKERY,
                    onClick = {
                        selectedDonorType = DonorType.BAKERY
                        donorName = "Artisan Sourdough Bakery"
                    },
                    modifier = Modifier.weight(1f)
                )
                DonorTypeOptionCard(
                    donorType = DonorType.GROCERY,
                    isSelected = selectedDonorType == DonorType.GROCERY,
                    onClick = {
                        selectedDonorType = DonorType.GROCERY
                        donorName = "Fresh Valley Organics"
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // AI Quick-Draft Assistant
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "✨ AI Quick-Draft Assistant",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = "Type raw details (e.g., 'We have 8 trays of wedding buffet chicken and rice, about 60 guests') and AI will auto-fill the whole form!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                OutlinedTextField(
                    value = rawAiPromptText,
                    onValueChange = { rawAiPromptText = it },
                    placeholder = { Text("e.g., 5 trays of pasta and 30 croissants left over...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_draft_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Preset Pill
                    SuggestionChip(
                        onClick = {
                            rawAiPromptText = if (selectedDonorType == DonorType.CELEBRATION_EVENT) {
                                "Wedding reception buffet surplus: 80 plates of grilled chicken, basmati rice pilaf, and mixed garden salad."
                            } else {
                                "Daily closing surplus: 45 portions of freshly baked sourdough loaves, baguettes and fruit pastries."
                            }
                        },
                        label = { Text("Use sample note", fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            onRequestAiDraft(rawAiPromptText, selectedDonorType)
                        },
                        enabled = !isAiDraftingLoading,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.testTag("ai_autofill_btn")
                    ) {
                        if (isAiDraftingLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("AI Auto-Fill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Form Fields
        Text(
            text = "2. Donation Details",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Meal / Food Title") },
            modifier = Modifier.fillMaxWidth().testTag("input_title"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Food Description & Condition") },
            modifier = Modifier.fillMaxWidth().testTag("input_desc"),
            shape = RoundedCornerShape(12.dp),
            minLines = 2
        )

        OutlinedTextField(
            value = donorName,
            onValueChange = { donorName = it },
            label = { Text("Donor Organization / Event Name") },
            modifier = Modifier.fillMaxWidth().testTag("input_donor_name"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (selectedDonorType == DonorType.CELEBRATION_EVENT) {
            OutlinedTextField(
                value = eventDetails,
                onValueChange = { eventDetails = it },
                label = { Text("Event Venue / Reception Hall Details") },
                modifier = Modifier.fillMaxWidth().testTag("input_event_details"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Servings & Weight Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = servings.toString(),
                onValueChange = { servings = it.toIntOrNull() ?: servings },
                label = { Text("Servings / Meals") },
                leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                modifier = Modifier.weight(1f).testTag("input_servings"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = weightKg.toString(),
                onValueChange = { weightKg = it.toDoubleOrNull() ?: weightKg },
                label = { Text("Est. Weight (kg)") },
                leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                modifier = Modifier.weight(1f).testTag("input_weight"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Storage & Freshness Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = expiryHours.toString(),
                onValueChange = { expiryHours = it.toIntOrNull() ?: expiryHours },
                label = { Text("Safe Window (Hours)") },
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.weight(1f).testTag("input_expiry_hours"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = dietaryTags,
                onValueChange = { dietaryTags = it },
                label = { Text("Dietary Tags") },
                leadingIcon = { Icon(Icons.Default.Eco, contentDescription = null) },
                modifier = Modifier.weight(1.2f).testTag("input_dietary"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Location & Contact
        OutlinedTextField(
            value = pickupAddress,
            onValueChange = { pickupAddress = it },
            label = { Text("Pickup Address") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("input_address"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = contactPhone,
            onValueChange = { contactPhone = it },
            label = { Text("Contact Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("input_phone"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = specialInstructions,
            onValueChange = { specialInstructions = it },
            label = { Text("Special Pickup Instructions (Loading bay, dock, contact person)") },
            modifier = Modifier.fillMaxWidth().testTag("input_instructions"),
            shape = RoundedCornerShape(12.dp),
            minLines = 2
        )

        // Submit Button
        Button(
            onClick = {
                onSubmitDonation(
                    title,
                    description,
                    donorName,
                    selectedDonorType,
                    category,
                    servings,
                    weightKg,
                    dietaryTags,
                    storageRequirement,
                    pickupAddress,
                    contactPhone,
                    specialInstructions,
                    eventDetails,
                    pickupWindow,
                    expiryHours
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("publish_donation_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.VolunteerActivism, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Publish Surplus Food for Rescue",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun DonorTypeOptionCard(
    donorType: DonorType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("donor_option_${donorType.name.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = donorType.iconLabel, fontSize = 20.sp)
            Text(
                text = donorType.displayName,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
