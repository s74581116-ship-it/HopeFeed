package com.example.data.model

enum class DonorType(val displayName: String, val iconLabel: String) {
    RESTAURANT("Restaurant", "🍽️"),
    BAKERY("Local Bakery", "🥖"),
    GROCERY("Grocery Store", "🥦"),
    CELEBRATION_EVENT("Wedding / Party Event", "🎉"),
    CATERER("Catering Service", "🍱"),
    COMMUNITY("Community Member", "🤝")
}

enum class FoodCategory(val displayName: String) {
    ALL("All"),
    COOKED_MEALS("Cooked Meals"),
    BAKED_GOODS("Baked Goods"),
    FRESH_PRODUCE("Fresh Produce"),
    BANQUET_BUFFET("Event Banquet"),
    DAIRY_CHILLED("Dairy & Chilled"),
    PACKAGED("Packaged / Dry")
}

enum class StorageRequirement(val displayName: String, val tempAdvice: String) {
    HOT_HOLD("Hot Insulated (Keep >60°C)", "Must be picked up within 2 hours"),
    REFRIGERATED("Refrigerated (Keep <4°C)", "Use insulated cooler bags"),
    FROZEN("Frozen (Keep <-18°C)", "Keep in deep freeze box"),
    AMBIENT("Room Temperature", "Dry, ventilated storage")
}

enum class DonationStatus(val displayName: String) {
    AVAILABLE("Available"),
    CLAIMED("Claimed"),
    IN_TRANSIT("Courier En Route"),
    DELIVERED("Delivered to Shelter"),
    EXPIRED("Expired")
}
