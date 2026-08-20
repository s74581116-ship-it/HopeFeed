package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.DonationStatus
import com.example.data.model.DonorType
import com.example.data.model.FoodCategory
import com.example.data.model.StorageRequirement

class Converters {
    @TypeConverter
    fun fromDonorType(value: DonorType): String = value.name

    @TypeConverter
    fun toDonorType(value: String): DonorType = runCatching { DonorType.valueOf(value) }.getOrDefault(DonorType.RESTAURANT)

    @TypeConverter
    fun fromFoodCategory(value: FoodCategory): String = value.name

    @TypeConverter
    fun toFoodCategory(value: String): FoodCategory = runCatching { FoodCategory.valueOf(value) }.getOrDefault(FoodCategory.COOKED_MEALS)

    @TypeConverter
    fun fromStorageRequirement(value: StorageRequirement): String = value.name

    @TypeConverter
    fun toStorageRequirement(value: String): StorageRequirement = runCatching { StorageRequirement.valueOf(value) }.getOrDefault(StorageRequirement.AMBIENT)

    @TypeConverter
    fun fromDonationStatus(value: DonationStatus): String = value.name

    @TypeConverter
    fun toDonationStatus(value: String): DonationStatus = runCatching { DonationStatus.valueOf(value) }.getOrDefault(DonationStatus.AVAILABLE)
}
