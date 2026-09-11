package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FarmHelperDao
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        InspectionRequestEntity::class,
        OrderEntity::class,
        NotificationEntity::class,
        RatingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FarmHelperDatabase : RoomDatabase() {

    abstract fun farmHelperDao(): FarmHelperDao

    companion object {
        @Volatile
        private var INSTANCE: FarmHelperDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FarmHelperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmHelperDatabase::class.java,
                    "farm_helper_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.farmHelperDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: FarmHelperDao) {
                // Seed Farmers & Buyers
                val farmers = listOf(
                    UserEntity(
                        id = 1,
                        username = "farmer_ramesh",
                        name = "Ramesh Patil",
                        role = "FARMER",
                        phone = "+91 98765 43210",
                        location = "Nashik, Maharashtra",
                        farmName = "Patil Organic Agro Farms",
                        rating = 4.9f,
                        ratingCount = 38,
                        isVerified = true,
                        avatarInitials = "RP"
                    ),
                    UserEntity(
                        id = 2,
                        username = "farmer_sunita",
                        name = "Sunita Choudhary",
                        role = "FARMER",
                        phone = "+91 98111 22334",
                        location = "Karnal, Haryana",
                        farmName = "Green Horizon FPO",
                        rating = 4.8f,
                        ratingCount = 24,
                        isVerified = true,
                        avatarInitials = "SC"
                    )
                )

                val buyers = listOf(
                    UserEntity(
                        id = 3,
                        username = "buyer_priya",
                        name = "Priya Sharma",
                        role = "BUYER",
                        phone = "+91 97234 56789",
                        location = "Pune, Maharashtra",
                        farmName = "",
                        rating = 5.0f,
                        ratingCount = 12,
                        isVerified = true,
                        avatarInitials = "PS"
                    ),
                    UserEntity(
                        id = 4,
                        username = "buyer_rohan",
                        name = "Rohan Mehta",
                        role = "BUYER",
                        phone = "+91 96321 09876",
                        location = "Mumbai, Maharashtra",
                        farmName = "",
                        rating = 4.9f,
                        ratingCount = 19,
                        isVerified = true,
                        avatarInitials = "RM"
                    )
                )

                dao.insertUsers(farmers + buyers)

                // Seed Products
                val products = listOf(
                    ProductEntity(
                        id = 1,
                        farmerId = 1,
                        farmerName = "Ramesh Patil",
                        farmerPhone = "+91 98765 43210",
                        title = "Organic Sharbati Wheat",
                        category = "Grains",
                        description = "Single-origin stone-ground wheat from black soil of Nashik. Sun-dried, unpolished, zero chemical pesticides.",
                        quantityAvailable = 50.0,
                        unit = "quintal",
                        pricePerUnit = 3200.0,
                        location = "Patil Agro Farms, Nashik",
                        qualityGrade = "Grade A+ Organic",
                        harvestDate = "10 Aug 2026",
                        isOrganicCertified = true,
                        allowsInspection = true,
                        emojiIcon = "🌾"
                    ),
                    ProductEntity(
                        id = 2,
                        farmerId = 1,
                        farmerName = "Ramesh Patil",
                        farmerPhone = "+91 98765 43210",
                        title = "Export Grade Alphonso Mangoes",
                        category = "Fruits",
                        description = "Naturally ripened GI-tagged Alphonso mangoes in hay boxes. Sweet aroma, saffron pulp, no calcium carbide used.",
                        quantityAvailable = 120.0,
                        unit = "crate (24 pcs)",
                        pricePerUnit = 1450.0,
                        location = "Ratnagiri Orchard / Nashik Depot",
                        qualityGrade = "Grade A Export",
                        harvestDate = "01 Sep 2026",
                        isOrganicCertified = true,
                        allowsInspection = true,
                        emojiIcon = "🥭"
                    ),
                    ProductEntity(
                        id = 3,
                        farmerId = 2,
                        farmerName = "Sunita Choudhary",
                        farmerPhone = "+91 98111 22334",
                        title = "Traditional Basmati Rice (1121)",
                        category = "Grains",
                        description = "Aged 2 years for optimal elongation and royal aroma. Direct from Karnal paddy fields.",
                        quantityAvailable = 35.0,
                        unit = "quintal",
                        pricePerUnit = 4800.0,
                        location = "Karnal Mandi Link, Haryana",
                        qualityGrade = "Grade A+ Premium",
                        harvestDate = "15 Jul 2026",
                        isOrganicCertified = true,
                        allowsInspection = true,
                        emojiIcon = "🍚"
                    ),
                    ProductEntity(
                        id = 4,
                        farmerId = 1,
                        farmerName = "Ramesh Patil",
                        farmerPhone = "+91 98765 43210",
                        title = "Desi Red Onions (Export Batch)",
                        category = "Vegetables",
                        description = "Pungent, dry skin, long shelf life red onions. Perfect for bulk storage or retail outlets.",
                        quantityAvailable = 80.0,
                        unit = "quintal",
                        pricePerUnit = 2100.0,
                        location = "Lasalgaon Road, Nashik",
                        qualityGrade = "Grade A Farm Fresh",
                        harvestDate = "28 Aug 2026",
                        isOrganicCertified = false,
                        allowsInspection = true,
                        emojiIcon = "🧅"
                    ),
                    ProductEntity(
                        id = 5,
                        farmerId = 2,
                        farmerName = "Sunita Choudhary",
                        farmerPhone = "+91 98111 22334",
                        title = "Pure Unadulterated Desi Ghee",
                        category = "Dairy",
                        description = "Bilona method cultured butter ghee from Gir cows grazing on green pastures. Golden granular texture.",
                        quantityAvailable = 60.0,
                        unit = "liter",
                        pricePerUnit = 1200.0,
                        location = "Green Horizon FPO Dairy, Karnal",
                        qualityGrade = "Grade A+ Lab Tested",
                        harvestDate = "05 Sep 2026",
                        isOrganicCertified = true,
                        allowsInspection = true,
                        emojiIcon = "🥛"
                    )
                )

                dao.insertProducts(products)

                // Seed Inspection Request
                val sampleInspection = InspectionRequestEntity(
                    id = 1,
                    productId = 1,
                    productTitle = "Organic Sharbati Wheat",
                    productEmoji = "🌾",
                    originalPricePerUnit = 3200.0,
                    unit = "quintal",
                    farmerId = 1,
                    farmerName = "Ramesh Patil",
                    buyerId = 3,
                    buyerName = "Priya Sharma",
                    buyerPhone = "+91 97234 56789",
                    inspectionType = "Farm Visit",
                    preferredDate = "14 Sep 2026",
                    preferredTime = "10:30 AM",
                    meetingLocation = "Patil Organic Farm Gate #2, Nashik",
                    buyerNotes = "Need to inspect moisture content and grain size for organic bakery.",
                    status = "VERIFIED_PASSED",
                    qualityGradeGiven = "Grade A+ Certified Verified",
                    qualityVerificationNotes = "Moisture tested at 11.2%, purity 99.8%, gluten strength excellent. Verified in presence of buyer.",
                    finalAgreedPrice = 3150.0,
                    requestedQuantity = 5.0
                )
                dao.insertInspection(sampleInspection)

                // Seed Sample Order
                val sampleOrder = OrderEntity(
                    id = 1,
                    orderNumber = "FH-2026-9021",
                    productId = 1,
                    productTitle = "Organic Sharbati Wheat",
                    productEmoji = "🌾",
                    farmerId = 1,
                    farmerName = "Ramesh Patil",
                    buyerId = 3,
                    buyerName = "Priya Sharma",
                    quantity = 5.0,
                    unit = "quintal",
                    unitPrice = 3150.0,
                    totalAmount = 15750.0,
                    deliveryAddress = "Priya Organic Bakes, FC Road, Shivaji Nagar, Pune - 411005",
                    paymentStatus = "ESCROW_PAID",
                    paymentMethod = "Escrow FarmPay (UPI ID: priya@okaxis)",
                    paymentTxnId = "TXN-AGRI-88219412",
                    orderStatus = "IN_TRANSIT",
                    trackingPartner = "Kisan Express Direct Logistics",
                    trackingNumber = "KED-MH-774109",
                    estimatedDeliveryDate = "15 Sep 2026",
                    isQualityVerified = true,
                    inspectionId = 1,
                    buyerRated = false,
                    farmerRated = false
                )
                dao.insertOrder(sampleOrder)

                // Seed Notifications
                val notifications = listOf(
                    NotificationEntity(
                        userId = 1,
                        role = "FARMER",
                        title = "Order Dispatched & In Transit",
                        message = "Order #FH-2026-9021 for Priya Sharma is in transit via Kisan Express. Escrow payment ₹15,750 secured.",
                        type = "DELIVERY",
                        referenceId = 1,
                        isRead = false
                    ),
                    NotificationEntity(
                        userId = 3,
                        role = "BUYER",
                        title = "Quality Verification Passed! 🌿",
                        message = "Organic Sharbati Wheat batch passed Grade A+ inspection with Ramesh Patil. Your order is on its way!",
                        type = "INSPECTION",
                        referenceId = 1,
                        isRead = false
                    ),
                    NotificationEntity(
                        userId = 1,
                        role = "FARMER",
                        title = "New 5-Star Rating Received ⭐",
                        message = "Buyer Rohan Mehta left a 5-star review: 'Outstanding produce purity and transparent weighing!'",
                        type = "RATING",
                        referenceId = 1,
                        isRead = true
                    )
                )
                for (n in notifications) {
                    dao.insertNotification(n)
                }

                // Seed Ratings
                val sampleRating = RatingEntity(
                    id = 1,
                    orderId = 0,
                    fromUserId = 4,
                    fromUserName = "Rohan Mehta",
                    fromRole = "BUYER",
                    toUserId = 1,
                    toUserName = "Ramesh Patil",
                    stars = 5,
                    tags = "Super Fresh, Accurate Weight, Fast Dispatch",
                    comment = "Ramesh Patil provided great transparent inspection of the Alphonso mango batch. Zero damages."
                )
                dao.insertRating(sampleRating)
            }
        }
    }
}
