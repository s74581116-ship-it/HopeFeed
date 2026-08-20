package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DonationStatus
import com.example.ui.components.DonationDetailDialog
import com.example.ui.screens.*
import com.example.ui.theme.HopeFeedTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.HopeFeedViewModel
import com.example.ui.viewmodel.UserRole

class MainActivity : ComponentActivity() {
    private val viewModel: HopeFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HopeFeedTheme {
                HopeFeedMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HopeFeedMainApp(viewModel: HopeFeedViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val donations by viewModel.donations.collectAsStateWithLifecycle()
    val shelters by viewModel.shelters.collectAsStateWithLifecycle()
    val impactStats by viewModel.impactStats.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.activeSimulationStatusMessage) {
        uiState.activeSimulationStatusMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearStatusMessage()
        }
    }

    var showRoleDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = "HopeFeed Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "HopeFeed",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Surplus Food Redistribution",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Role Switcher Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { showRoleDialog = true }
                            .testTag("role_switcher_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = uiState.userRole.badge, fontSize = 14.sp)
                            Text(
                                text = uiState.userRole.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Role",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 4.dp
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.FEED,
                    onClick = { viewModel.setTab(AppTab.FEED) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.FEED) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                            contentDescription = "Feed"
                        )
                    },
                    label = { Text("Feed", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_feed")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.MAP,
                    onClick = { viewModel.setTab(AppTab.MAP) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.MAP) Icons.Filled.Map else Icons.Outlined.Map,
                            contentDescription = "Live Map"
                        )
                    },
                    label = { Text("Live Map", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_map")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.AI_MATCH,
                    onClick = { viewModel.setTab(AppTab.AI_MATCH) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.AI_MATCH) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "AI Match"
                        )
                    },
                    label = { Text("AI Match", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_ai_match")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.DONATE,
                    onClick = { viewModel.setTab(AppTab.DONATE) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.DONATE) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                            contentDescription = "Donate"
                        )
                    },
                    label = { Text("Donate", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_donate")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.IMPACT,
                    onClick = { viewModel.setTab(AppTab.IMPACT) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.IMPACT) Icons.Filled.VolunteerActivism else Icons.Outlined.VolunteerActivism,
                            contentDescription = "Impact"
                        )
                    },
                    label = { Text("Impact", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_impact")
                )
            }
        },
        floatingActionButton = {
            if (uiState.currentTab == AppTab.FEED || uiState.currentTab == AppTab.MAP) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setTab(AppTab.DONATE) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Donate Food") },
                    text = { Text("Donate Surplus", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("fab_donate_surplus")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.FEED -> {
                    FeedScreen(
                        donations = donations,
                        impactStats = impactStats,
                        selectedDonorFilter = uiState.selectedDonorTypeFilter,
                        selectedCategoryFilter = uiState.selectedCategoryFilter,
                        searchQuery = uiState.searchQuery,
                        onDonorFilterChange = { viewModel.setDonorFilter(it) },
                        onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onDonationClick = { viewModel.selectDonationDetail(it) },
                        onAiMatchClick = { viewModel.triggerAiMatchForDonation(it) },
                        onNavigateToDonate = { viewModel.setTab(AppTab.DONATE) },
                        onNavigateToMap = { viewModel.setTab(AppTab.MAP) }
                    )
                }
                AppTab.MAP -> {
                    MapScreen(
                        donations = donations,
                        shelters = shelters,
                        onDonationClick = { viewModel.selectDonationDetail(it) },
                        onAiMatchClick = { viewModel.triggerAiMatchForDonation(it) }
                    )
                }
                AppTab.AI_MATCH -> {
                    AiMatchScreen(
                        donations = donations,
                        shelters = shelters,
                        selectedDonation = uiState.aiSelectedDonationForMatching,
                        aiRecommendations = uiState.aiRecommendations,
                        isLoading = uiState.isAiMatchingLoading,
                        onSelectDonationForMatch = { viewModel.triggerAiMatchForDonation(it) },
                        onClaimMatch = { donationId, shelter ->
                            viewModel.claimDonation(donationId, shelter)
                        }
                    )
                }
                AppTab.DONATE -> {
                    DonateScreen(
                        lastAiDraftResult = uiState.lastAiDraftResult,
                        isAiDraftingLoading = uiState.isAiDraftingLoading,
                        onRequestAiDraft = { notes, donorType ->
                            viewModel.requestAiDraft(notes, donorType)
                        },
                        onSubmitDonation = { title, desc, donor, type, cat, serv, weight, diet, storage, addr, phone, instr, evt, win, exp ->
                            viewModel.createDonation(
                                title = title,
                                description = desc,
                                donorName = donor,
                                donorType = type,
                                category = cat,
                                servings = serv,
                                weightKg = weight,
                                dietaryTags = diet,
                                storageRequirement = storage,
                                pickupAddress = addr,
                                contactPhone = phone,
                                specialInstructions = instr,
                                eventDetails = evt,
                                pickupWindow = win,
                                expiryHours = exp
                            )
                        }
                    )
                }
                AppTab.IMPACT -> {
                    ImpactScreen(
                        impactStats = impactStats,
                        shelters = shelters,
                        activeDeliveries = donations,
                        currentUserRole = uiState.userRole,
                        onRoleChange = { viewModel.setUserRole(it) },
                        onAdvanceDeliveryStatus = { id, status ->
                            viewModel.advanceDonationStatus(id, status)
                        }
                    )
                }
            }
        }
    }

    // Donation Detail Modal
    if (uiState.selectedDonationDetail != null) {
        DonationDetailDialog(
            donation = uiState.selectedDonationDetail!!,
            shelters = shelters,
            onDismiss = { viewModel.selectDonationDetail(null) },
            onAiMatch = {
                val d = uiState.selectedDonationDetail!!
                viewModel.selectDonationDetail(null)
                viewModel.triggerAiMatchForDonation(d)
            },
            onClaimByShelter = { shelter ->
                val d = uiState.selectedDonationDetail!!
                viewModel.claimDonation(d.id, shelter)
            },
            onAdvanceStatus = { nextStatus ->
                val d = uiState.selectedDonationDetail!!
                viewModel.advanceDonationStatus(d.id, nextStatus)
            }
        )
    }

    // Role Switcher Dialog
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Select Active Portal View", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Switch between donor posting, shelter recipient claiming, and volunteer courier delivery modes:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    UserRole.values().forEach { role ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.userRole == role) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setUserRole(role)
                                    showRoleDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = role.badge, fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = role.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
