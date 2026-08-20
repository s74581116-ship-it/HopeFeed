package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_donations")
data class FoodDonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val donorName: String,
    val donorType: DonorType,
    val category: FoodCategory,
    val servings: Int,
    val weightKg: Double,
    val dietaryTagsCsv: String, // "Vegetarian, Halal, Nut-Free"
    val storageRequirement: StorageRequirement,
    val pickupAddress: String,
    val latitude: Double,
    val longitude: Double,
    val contactPhone: String,
    val specialInstructions: String,
    val eventDetails: String = "", // e.g., "Grand Palace Wedding Reception - Hall B"
    val pickupWindow: String, // e.g., "Today 15:00 - 18:30"
    val expiryTimeHours: Int, // hours remaining for freshness
    val status: DonationStatus = DonationStatus.AVAILABLE,
    val claimedShelterId: Long? = null,
    val claimedShelterName: String? = null,
    val courierName: String? = null,
    val co2SavedKg: Double = servings * 1.45,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Community Kitchen", "Homeless Shelter", "Youth Haven", "Food Bank Hub"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val currentOccupancy: Int,
    val urgentNeedNote: String,
    val contactPhone: String,
    val operatingHours: String,
    val acceptedCategoriesCsv: String
)

data class AiMatchRecommendation(
    val donationId: Long,
    val shelterId: Long,
    val shelterName: String,
    val shelterAddress: String,
    val matchScore: Int,
    val distanceKm: Double,
    val etaMinutes: Int,
    val reasoning: String,
    val priorityLevel: String, // "CRITICAL", "HIGH", "OPTIMAL"
    val safetyGuidelines: String
)

data class ImpactStats(
    val totalMealsRescued: Int,
    val totalKgRescued: Double,
    val totalCo2SavedKg: Double,
    val activeDonationsCount: Int,
    val totalSheltersSupported: Int,
    val totalVolunteersActive: Int
)
