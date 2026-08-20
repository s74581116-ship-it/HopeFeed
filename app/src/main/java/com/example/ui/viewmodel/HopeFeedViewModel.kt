package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiMatchingEngine
import com.example.data.ai.DonationDraftResult
import com.example.data.model.*
import com.example.data.repository.FoodRescueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    FEED("Surplus Feed"),
    MAP("Live Logistics Map"),
    AI_MATCH("AI Matchmaker"),
    DONATE("Donate Food"),
    IMPACT("Shelters & Impact")
}

data class HopeFeedUiState(
    val currentTab: AppTab = AppTab.FEED,
    val selectedDonorTypeFilter: DonorType? = null,
    val selectedCategoryFilter: FoodCategory = FoodCategory.ALL,
    val searchQuery: String = "",
    val selectedDonationDetail: FoodDonationEntity? = null,
    val selectedShelterDetail: ShelterEntity? = null,
    val isAiMatchingLoading: Boolean = false,
    val aiRecommendations: List<AiMatchRecommendation> = emptyList(),
    val aiSelectedDonationForMatching: FoodDonationEntity? = null,
    val isAiDraftingLoading: Boolean = false,
    val lastAiDraftResult: DonationDraftResult? = null,
    val activeSimulationStatusMessage: String? = null,
    val userRole: UserRole = UserRole.DONOR_PARTNER
)

enum class UserRole(val label: String, val badge: String) {
    DONOR_PARTNER("Food Donor", "👨‍🍳"),
    SHELTER_RECIPIENT("Shelter / Food Bank", "🏠"),
    VOLUNTEER_COURIER("Volunteer Logistics", "🚴")
}

class HopeFeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRescueRepository.create(application)

    val donations: StateFlow<List<FoodDonationEntity>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shelters: StateFlow<List<ShelterEntity>> = repository.allShelters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val impactStats: StateFlow<ImpactStats> = repository.impactStats
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ImpactStats(1420, 580.0, 1450.0, 4, 8, 24)
        )

    private val _uiState = MutableStateFlow(HopeFeedUiState())
    val uiState: StateFlow<HopeFeedUiState> = _uiState.asStateFlow()

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setDonorFilter(filter: DonorType?) {
        _uiState.update { it.copy(selectedDonorTypeFilter = filter) }
    }

    fun setCategoryFilter(category: FoodCategory) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectDonationDetail(donation: FoodDonationEntity?) {
        _uiState.update { it.copy(selectedDonationDetail = donation) }
    }

    fun selectShelterDetail(shelter: ShelterEntity?) {
        _uiState.update { it.copy(selectedShelterDetail = shelter) }
    }

    fun setUserRole(role: UserRole) {
        _uiState.update { it.copy(userRole = role) }
    }

    fun triggerAiMatchForDonation(donation: FoodDonationEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    isAiMatchingLoading = true,
                    aiSelectedDonationForMatching = donation,
                    currentTab = AppTab.AI_MATCH
                )
            }
            val currentShelters = shelters.value
            val recommendations = AiMatchingEngine.getSmartRecommendations(donation, currentShelters)
            _uiState.update {
                it.copy(
                    isAiMatchingLoading = false,
                    aiRecommendations = recommendations
                )
            }
        }
    }

    fun claimDonation(donationId: Long, shelter: ShelterEntity, courierName: String = "HopeFeed Express Courier") {
        viewModelScope.launch {
            repository.claimDonation(
                donationId = donationId,
                shelterId = shelter.id,
                shelterName = shelter.name,
                courierName = courierName
            )
            _uiState.update {
                it.copy(
                    selectedDonationDetail = null,
                    activeSimulationStatusMessage = "Successfully matched & claimed by ${shelter.name}! Courier assigned."
                )
            }
        }
    }

    fun advanceDonationStatus(donationId: Long, nextStatus: DonationStatus) {
        viewModelScope.launch {
            repository.updateDonationStatus(donationId, nextStatus)
        }
    }

    fun createDonation(
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
    ) {
        viewModelScope.launch {
            // Coordinate mock near downtown SF cluster with minor jitter
            val lat = 37.7749 + (Math.random() - 0.5) * 0.05
            val lon = -122.4194 + (Math.random() - 0.5) * 0.05

            val newDonation = FoodDonationEntity(
                title = title,
                description = description,
                donorName = donorName,
                donorType = donorType,
                category = category,
                servings = servings,
                weightKg = weightKg,
                dietaryTagsCsv = dietaryTags,
                storageRequirement = storageRequirement,
                pickupAddress = pickupAddress,
                latitude = lat,
                longitude = lon,
                contactPhone = contactPhone,
                specialInstructions = specialInstructions,
                eventDetails = eventDetails,
                pickupWindow = pickupWindow,
                expiryTimeHours = expiryHours,
                status = DonationStatus.AVAILABLE
            )
            val newId = repository.addDonation(newDonation)
            _uiState.update {
                it.copy(
                    currentTab = AppTab.FEED,
                    activeSimulationStatusMessage = "Surplus donation published! AI matchmaking activated for shelters."
                )
            }
        }
    }

    fun requestAiDraft(notes: String, donorType: DonorType) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isAiDraftingLoading = true) }
            val draft = AiMatchingEngine.autoDraftDonation(notes, donorType)
            _uiState.update {
                it.copy(
                    isAiDraftingLoading = false,
                    lastAiDraftResult = draft
                )
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(activeSimulationStatusMessage = null) }
    }
}
