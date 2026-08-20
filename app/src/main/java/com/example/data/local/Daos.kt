package com.example.data.local

import androidx.room.*
import com.example.data.model.DonationStatus
import com.example.data.model.FoodDonationEntity
import com.example.data.model.ShelterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_donations ORDER BY createdAt DESC")
    fun getAllDonations(): Flow<List<FoodDonationEntity>>

    @Query("SELECT * FROM food_donations WHERE status = :status ORDER BY createdAt DESC")
    fun getDonationsByStatus(status: DonationStatus): Flow<List<FoodDonationEntity>>

    @Query("SELECT * FROM food_donations WHERE id = :id")
    suspend fun getDonationById(id: Long): FoodDonationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: FoodDonationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonations(donations: List<FoodDonationEntity>)

    @Update
    suspend fun updateDonation(donation: FoodDonationEntity)

    @Query("UPDATE food_donations SET status = :status, claimedShelterId = :shelterId, claimedShelterName = :shelterName, courierName = :courierName WHERE id = :donationId")
    suspend fun updateClaimStatus(
        donationId: Long,
        status: DonationStatus,
        shelterId: Long?,
        shelterName: String?,
        courierName: String?
    )

    @Delete
    suspend fun deleteDonation(donation: FoodDonationEntity)

    @Query("DELETE FROM food_donations")
    suspend fun clearAll()
}

@Dao
interface ShelterDao {
    @Query("SELECT * FROM shelters ORDER BY name ASC")
    fun getAllShelters(): Flow<List<ShelterEntity>>

    @Query("SELECT * FROM shelters WHERE id = :id")
    suspend fun getShelterById(id: Long): ShelterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelters(shelters: List<ShelterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelter(shelter: ShelterEntity): Long
}
