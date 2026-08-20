package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.FoodDao
import com.example.data.local.ShelterDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FoodRescueRepository(
    private val foodDao: FoodDao,
    private val shelterDao: ShelterDao
) {
    val allDonations: Flow<List<FoodDonationEntity>> = foodDao.getAllDonations()
    val allShelters: Flow<List<ShelterEntity>> = shelterDao.getAllShelters()

    val impactStats: Flow<ImpactStats> = allDonations.map { donations ->
        val delivered = donations.filter { it.status == DonationStatus.DELIVERED || it.status == DonationStatus.IN_TRANSIT || it.status == DonationStatus.CLAIMED }
        val allCompletedOrActive = donations.filter { it.status != DonationStatus.EXPIRED }

        val mealsRescued = allCompletedOrActive.sumOf { it.servings } + 1420 // Base cumulative impact
        val kgRescued = allCompletedOrActive.sumOf { it.weightKg } + 580.0
        val co2Saved = (kgRescued * 2.5)

        ImpactStats(
            totalMealsRescued = mealsRescued,
            totalKgRescued = (kgRescued * 10).toLong() / 10.0,
            totalCo2SavedKg = (co2Saved * 10).toLong() / 10.0,
            activeDonationsCount = donations.count { it.status == DonationStatus.AVAILABLE },
            totalSheltersSupported = 8,
            totalVolunteersActive = 24
        )
    }

    suspend fun addDonation(donation: FoodDonationEntity): Long {
        return foodDao.insertDonation(donation)
    }

    suspend fun claimDonation(
        donationId: Long,
        shelterId: Long,
        shelterName: String,
        courierName: String
    ) {
        foodDao.updateClaimStatus(
            donationId = donationId,
            status = DonationStatus.CLAIMED,
            shelterId = shelterId,
            shelterName = shelterName,
            courierName = courierName
        )
    }

    suspend fun updateDonationStatus(donationId: Long, status: DonationStatus) {
        val existing = foodDao.getDonationById(donationId) ?: return
        foodDao.updateDonation(existing.copy(status = status))
    }

    suspend fun seedInitialDataIfEmpty() {
        val sampleShelters = listOf(
            ShelterEntity(
                name = "St. Vincent Community Kitchen",
                category = "Community Soup Kitchen",
                address = "420 Mission Blvd, Downtown",
                latitude = 37.7749,
                longitude = -122.4194,
                capacity = 120,
                currentOccupancy = 95,
                urgentNeedNote = "High - Evening Dinner Serving for 90+ guests",
                contactPhone = "+1 (555) 234-5678",
                operatingHours = "07:00 AM - 09:00 PM",
                acceptedCategoriesCsv = "Cooked Meals, Event Banquet, Fresh Produce, Baked Goods"
            ),
            ShelterEntity(
                name = "Hope Valley Youth & Family Shelter",
                category = "Youth & Family Haven",
                address = "850 Pine Grove Ave, Westside",
                latitude = 37.7833,
                longitude = -122.4167,
                capacity = 75,
                currentOccupancy = 68,
                urgentNeedNote = "Urgent - Weekend Family Breakfast & Lunch Packs",
                contactPhone = "+1 (555) 345-6789",
                operatingHours = "24/7 Operations",
                acceptedCategoriesCsv = "Baked Goods, Dairy & Chilled, Packaged, Cooked Meals"
            ),
            ShelterEntity(
                name = "Metropolitan Food Bank Hub",
                category = "Central Redistribution Hub",
                address = "1200 Industrial Pkwy, East Bay",
                latitude = 37.7690,
                longitude = -122.4080,
                capacity = 350,
                currentOccupancy = 180,
                urgentNeedNote = "Bulk Logistics & Cold Storage Ready",
                contactPhone = "+1 (555) 456-7890",
                operatingHours = "06:00 AM - 10:00 PM",
                acceptedCategoriesCsv = "Fresh Produce, Dairy & Chilled, Packaged, Banquet Buffets"
            ),
            ShelterEntity(
                name = "Oasis Women & Children's Sanctuary",
                category = "Emergency Shelter",
                address = "310 Elmwood Terrace, North Hills",
                latitude = 37.7895,
                longitude = -122.4280,
                capacity = 60,
                currentOccupancy = 54,
                urgentNeedNote = "Needs Fresh Fruits, Milk & Prepared Warm Soups",
                contactPhone = "+1 (555) 567-8901",
                operatingHours = "24/7 Operations",
                acceptedCategoriesCsv = "Cooked Meals, Dairy, Fresh Produce, Baked Goods"
            )
        )
        shelterDao.insertShelters(sampleShelters)

        val sampleDonations = listOf(
            FoodDonationEntity(
                title = "Gourmet Pasta, Lasagna & Garlic Bread Platters",
                description = "Freshly prepared hotel-grade pasta trays and artisan garlic loaves from lunch service. Kept in hot-holding units.",
                donorName = "Trattoria Bella Roma",
                donorType = DonorType.RESTAURANT,
                category = FoodCategory.COOKED_MEALS,
                servings = 40,
                weightKg = 16.5,
                dietaryTagsCsv = "Vegetarian Options, Nut-Free, Halal Friendly",
                storageRequirement = StorageRequirement.HOT_HOLD,
                pickupAddress = "580 Columbus Ave, Little Italy",
                latitude = 37.7985,
                longitude = -122.4085,
                contactPhone = "+1 (555) 789-0123",
                specialInstructions = "Park in rear loading bay. Ask for Chef Mario at kitchen door.",
                pickupWindow = "Today: Ready for pickup (Next 2 hrs)",
                expiryTimeHours = 3,
                status = DonationStatus.AVAILABLE
            ),
            FoodDonationEntity(
                title = "Wedding Reception Banquet Buffet Surplus",
                description = "Untouched catered trays of roasted vegetables, herb rice pilaf, glazed paneer, and grilled chicken breast from grand wedding celebration.",
                donorName = "Sarah & Liam's Wedding Reception",
                donorType = DonorType.CELEBRATION_EVENT,
                category = FoodCategory.BANQUET_BUFFET,
                servings = 85,
                weightKg = 34.0,
                dietaryTagsCsv = "Halal, Gluten-Free Options, Nut-Free",
                storageRequirement = StorageRequirement.HOT_HOLD,
                pickupAddress = "Grand Royale Ballroom, 777 Marina Blvd",
                latitude = 37.8040,
                longitude = -122.4350,
                contactPhone = "+1 (555) 890-1234",
                specialInstructions = "Event Coordinator Sarah at Entrance 2. Insulated transport bins ready.",
                eventDetails = "Grand Royale Ballroom - Reception Banquet",
                pickupWindow = "Immediate Pickup (Before 8:00 PM)",
                expiryTimeHours = 2,
                status = DonationStatus.AVAILABLE
            ),
            FoodDonationEntity(
                title = "Artisan Sourdough, Baguettes & Morning Pastries",
                description = "Daily bake surplus: organic sourdough loaves, flaky butter croissants, and seeded brioche rolls baked fresh this morning.",
                donorName = "Golden Crust Bakery",
                donorType = DonorType.BAKERY,
                category = FoodCategory.BAKED_GOODS,
                servings = 55,
                weightKg = 18.0,
                dietaryTagsCsv = "Vegetarian, Vegan Sourdough",
                storageRequirement = StorageRequirement.AMBIENT,
                pickupAddress = "240 Castro St",
                latitude = 37.7635,
                longitude = -122.4355,
                contactPhone = "+1 (555) 901-2345",
                specialInstructions = "Side alley door, bakery boxes already sealed with bread ties.",
                pickupWindow = "Today: 4:00 PM - 7:30 PM",
                expiryTimeHours = 18,
                status = DonationStatus.AVAILABLE
            ),
            FoodDonationEntity(
                title = "Organic Fresh Produce Crates & Fruit Boxes",
                description = "Crates of organic apples, ripe bananas, bagged spinach, bell peppers, and carrots from morning inventory refresh.",
                donorName = "Green Valley Organic Market",
                donorType = DonorType.GROCERY,
                category = FoodCategory.FRESH_PRODUCE,
                servings = 90,
                weightKg = 42.0,
                dietaryTagsCsv = "100% Organic, Vegan, Gluten-Free",
                storageRequirement = StorageRequirement.AMBIENT,
                pickupAddress = "1500 Market St, Financial District",
                latitude = 37.7770,
                longitude = -122.4180,
                contactPhone = "+1 (555) 012-3456",
                specialInstructions = "Loading Dock #3, ask for inventory manager Dave.",
                pickupWindow = "Today: 2:00 PM - 9:00 PM",
                expiryTimeHours = 48,
                status = DonationStatus.AVAILABLE
            ),
            FoodDonationEntity(
                title = "Birthday Gala Fiesta Platters & Finger Foods",
                description = "Assorted gourmet mini sandwiches, vegetable spring rolls, hummus dip platters, and fruit skewers from sweet 16 party.",
                donorName = "Miller Family Birthday Celebration",
                donorType = DonorType.CELEBRATION_EVENT,
                category = FoodCategory.BANQUET_BUFFET,
                servings = 45,
                weightKg = 14.0,
                dietaryTagsCsv = "Vegetarian Friendly, Dairy-Free Options",
                storageRequirement = StorageRequirement.REFRIGERATED,
                pickupAddress = "Clubhouse Community Hall, 300 Sunset Way",
                latitude = 37.7550,
                longitude = -122.4450,
                contactPhone = "+1 (555) 123-9876",
                specialInstructions = "Main clubhouse lobby, containers already chilled.",
                eventDetails = "Sunset Community Clubhouse - Private Gala",
                pickupWindow = "Today: 5:00 PM - 8:30 PM",
                expiryTimeHours = 6,
                status = DonationStatus.CLAIMED,
                claimedShelterId = 1,
                claimedShelterName = "St. Vincent Community Kitchen",
                courierName = "Volunteer Elena R. (EV Cargo Van)"
            )
        )
        foodDao.insertDonations(sampleDonations)
    }

    companion object {
        fun create(context: Context): FoodRescueRepository {
            val db = AppDatabase.getInstance(context)
            val repo = FoodRescueRepository(db.foodDao(), db.shelterDao())
            CoroutineScope(Dispatchers.IO).launch {
                repo.seedInitialDataIfEmpty()
            }
            return repo
        }
    }
}
