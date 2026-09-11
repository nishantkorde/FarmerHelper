package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.dao.FarmHelperDao
import com.example.data.database.FarmHelperDatabase
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import com.example.data.repository.FarmRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FarmHelperRobolectricTest {

    private lateinit var database: FarmHelperDatabase
    private lateinit var dao: FarmHelperDao
    private lateinit var repository: FarmRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FarmHelperDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.farmHelperDao()
        repository = FarmRepository(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testFarmerAndBuyerWorkflow() = runBlocking {
        // 1. Insert Farmer & Buyer
        val farmer = UserEntity(
            username = "ramesh_patil",
            name = "Ramesh Patil",
            role = "FARMER",
            phone = "+91 98220 11223",
            location = "Nashik, Maharashtra",
            farmName = "Patil Organic Agro Farms",
            rating = 4.9f,
            ratingCount = 38,
            isVerified = true,
            avatarInitials = "RP"
        )
        val farmerId = repository.insertUser(farmer)

        val buyer = UserEntity(
            username = "priya_sharma",
            name = "Priya Sharma",
            role = "BUYER",
            phone = "+91 94221 88990",
            location = "Pune, Maharashtra",
            farmName = "",
            rating = 5.0f,
            ratingCount = 12,
            isVerified = true,
            avatarInitials = "PS"
        )
        val buyerId = repository.insertUser(buyer)

        val savedFarmer = repository.getUserById(farmerId)
        assertNotNull(savedFarmer)
        assertEquals("FARMER", savedFarmer?.role)

        // 2. Farmer lists a product
        val product = ProductEntity(
            farmerId = farmerId,
            farmerName = savedFarmer!!.name,
            farmerPhone = savedFarmer.phone,
            title = "Organic Sharbati Wheat",
            category = "Grains",
            description = "100% natural, pesticide-free golden wheat.",
            quantityAvailable = 45.0,
            unit = "quintal",
            pricePerUnit = 2600.0,
            location = savedFarmer.location,
            qualityGrade = "Grade A+ Organic",
            harvestDate = "02 Sep 2026",
            isOrganicCertified = true,
            allowsInspection = true,
            emojiIcon = "🌾"
        )
        val productId = repository.insertProduct(product)
        val products = repository.allProducts.first()
        assertEquals(1, products.size)
        assertEquals("Organic Sharbati Wheat", products[0].title)

        // 3. Buyer requests Quality Inspection (Meet/Inspect)
        val savedBuyer = repository.getUserById(buyerId)!!
        val inspectionId = repository.requestInspection(
            product = products[0],
            buyer = savedBuyer,
            inspectionType = "Farm Visit",
            preferredDate = "12 Sep 2026",
            preferredTime = "10:00 AM",
            meetingLocation = "Patil Farm, Dindori Road, Nashik",
            notes = "Check moisture %, grain purity, seed cleanliness",
            requestedQuantity = 10.0
        )
        val inspections = repository.allInspections.first()
        assertEquals(1, inspections.size)
        assertEquals("PENDING", inspections[0].status)

        // Farmer received notification
        val farmerNotifs = repository.getNotificationsForUser(farmerId).first()
        assertTrue(farmerNotifs.any { it.type == "INSPECTION" })

        // 4. Farmer accepts inspection
        repository.acceptInspection(inspections[0])
        val acceptedInspection = repository.allInspections.first()[0]
        assertEquals("ACCEPTED", acceptedInspection.status)

        // 5. Quality verified & Deal confirmed
        repository.verifyQualityAndConfirmDeal(
            inspection = acceptedInspection,
            qualityGrade = "Grade A+ Certified Organic",
            notes = "Moisture tested at 11.2%, purity 99.9%. Deal confirmed.",
            agreedPrice = 2550.0
        )
        val verifiedInspection = repository.allInspections.first()[0]
        assertEquals("VERIFIED_PASSED", verifiedInspection.status)
        assertEquals(2550.0, verifiedInspection.finalAgreedPrice, 0.01)

        // 6. Buyer places order with Secure Escrow Payment
        val orderId = repository.placeOrderWithPayment(
            product = products[0],
            buyer = savedBuyer,
            quantity = 10.0,
            unitPrice = 2550.0,
            deliveryAddress = "Flat 402, Green Meadows, Pune - 411016",
            paymentMethod = "UPI (Google Pay)",
            isQualityVerified = true,
            inspectionId = verifiedInspection.id
        )
        val orders = repository.allOrders.first()
        assertEquals(1, orders.size)
        assertEquals("ESCROW_PAID", orders[0].paymentStatus)
        assertEquals(25500.0, orders[0].totalAmount, 0.01)

        // Stock decreased accordingly
        val updatedProduct = repository.allProducts.first()[0]
        assertEquals(35.0, updatedProduct.quantityAvailable, 0.01)

        // 7. Dispatch & Delivery
        repository.updateOrderStatus(orders[0], "DISPATCHED")
        repository.updateOrderStatus(orders[0], "IN_TRANSIT")
        repository.updateOrderStatus(orders[0], "DELIVERED")

        val deliveredOrder = repository.allOrders.first()[0]
        assertEquals("DELIVERED", deliveredOrder.orderStatus)
        assertEquals("RELEASED_TO_FARMER", deliveredOrder.paymentStatus)

        // 8. Automated Rating System
        repository.submitRating(
            order = deliveredOrder,
            fromUser = savedBuyer,
            toUser = savedFarmer,
            stars = 5,
            tags = "Super Fresh 🌿, Accurate Weight ⚖️",
            comment = "Outstanding produce quality verified in person!"
        )

        val ratings = repository.allRatings.first()
        assertEquals(1, ratings.size)
        assertEquals(5, ratings[0].stars)
        assertEquals("Super Fresh 🌿, Accurate Weight ⚖️", ratings[0].tags)
    }
}
