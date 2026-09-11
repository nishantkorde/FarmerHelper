package com.example.data.repository

import com.example.data.dao.FarmHelperDao
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FarmRepository(private val dao: FarmHelperDao) {

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    fun getUsersByRole(role: String): Flow<List<UserEntity>> = dao.getUsersByRole(role)
    suspend fun getUserById(id: Long): UserEntity? = dao.getUserById(id)
    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    // --- Products ---
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    fun getProductsByFarmer(farmerId: Long): Flow<List<ProductEntity>> = dao.getProductsByFarmer(farmerId)
    suspend fun insertProduct(product: ProductEntity): Long = dao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
    suspend fun deleteProduct(productId: Long) = dao.deleteProduct(productId)

    // --- Inspections ---
    val allInspections: Flow<List<InspectionRequestEntity>> = dao.getAllInspections()
    fun getInspectionsForFarmer(farmerId: Long): Flow<List<InspectionRequestEntity>> =
        dao.getInspectionsForFarmer(farmerId)
    fun getInspectionsForBuyer(buyerId: Long): Flow<List<InspectionRequestEntity>> =
        dao.getInspectionsForBuyer(buyerId)

    suspend fun requestInspection(
        product: ProductEntity,
        buyer: UserEntity,
        inspectionType: String,
        preferredDate: String,
        preferredTime: String,
        meetingLocation: String,
        notes: String,
        requestedQuantity: Double
    ): Long {
        val inspection = InspectionRequestEntity(
            productId = product.id,
            productTitle = product.title,
            productEmoji = product.emojiIcon,
            originalPricePerUnit = product.pricePerUnit,
            unit = product.unit,
            farmerId = product.farmerId,
            farmerName = product.farmerName,
            buyerId = buyer.id,
            buyerName = buyer.name,
            buyerPhone = buyer.phone,
            inspectionType = inspectionType,
            preferredDate = preferredDate,
            preferredTime = preferredTime,
            meetingLocation = meetingLocation,
            buyerNotes = notes,
            status = "PENDING",
            finalAgreedPrice = product.pricePerUnit,
            requestedQuantity = requestedQuantity
        )
        val inspectionId = dao.insertInspection(inspection)

        // Real-time notification to farmer
        dao.insertNotification(
            NotificationEntity(
                userId = product.farmerId,
                role = "FARMER",
                title = "New Quality Inspection Request",
                message = "${buyer.name} requested a $inspectionType for '${product.title}' on $preferredDate at $preferredTime.",
                type = "INSPECTION",
                referenceId = inspectionId
            )
        )

        return inspectionId
    }

    suspend fun acceptInspection(inspection: InspectionRequestEntity) {
        val updated = inspection.copy(
            status = "ACCEPTED",
            updatedAt = System.currentTimeMillis()
        )
        dao.updateInspection(updated)

        // Notify Buyer
        dao.insertNotification(
            NotificationEntity(
                userId = inspection.buyerId,
                role = "BUYER",
                title = "Inspection Request Accepted! 🤝",
                message = "Farmer ${inspection.farmerName} confirmed your inspection for '${inspection.productTitle}' on ${inspection.preferredDate} at ${inspection.preferredTime} at ${inspection.meetingLocation}.",
                type = "INSPECTION",
                referenceId = inspection.id
            )
        )
    }

    suspend fun verifyQualityAndConfirmDeal(
        inspection: InspectionRequestEntity,
        qualityGrade: String,
        notes: String,
        agreedPrice: Double
    ) {
        val updated = inspection.copy(
            status = "VERIFIED_PASSED",
            qualityGradeGiven = qualityGrade,
            qualityVerificationNotes = notes,
            finalAgreedPrice = agreedPrice,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateInspection(updated)

        // Real-time notification to Buyer: Quality verified, deal confirmed!
        dao.insertNotification(
            NotificationEntity(
                userId = inspection.buyerId,
                role = "BUYER",
                title = "Quality Verification Passed & Deal Confirmed! ✅",
                message = "'${inspection.productTitle}' passed inspection ($qualityGrade). Price agreed: ₹$agreedPrice / ${inspection.unit}. You can now place your order securely!",
                type = "INSPECTION",
                referenceId = inspection.id
            )
        )

        // Real-time notification to Farmer
        dao.insertNotification(
            NotificationEntity(
                userId = inspection.farmerId,
                role = "FARMER",
                title = "Quality Verified for ${inspection.buyerName}",
                message = "Quality inspection confirmed for '${inspection.productTitle}'. Waiting for buyer checkout.",
                type = "INSPECTION",
                referenceId = inspection.id
            )
        )
    }

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    fun getOrdersForFarmer(farmerId: Long): Flow<List<OrderEntity>> = dao.getOrdersForFarmer(farmerId)
    fun getOrdersForBuyer(buyerId: Long): Flow<List<OrderEntity>> = dao.getOrdersForBuyer(buyerId)

    suspend fun placeOrderWithPayment(
        product: ProductEntity,
        buyer: UserEntity,
        quantity: Double,
        unitPrice: Double,
        deliveryAddress: String,
        paymentMethod: String,
        isQualityVerified: Boolean,
        inspectionId: Long?
    ): Long {
        val totalAmount = quantity * unitPrice
        val randomOrderCode = "FH-" + (1000..9999).random()
        val txnId = "TXN-" + UUID.randomUUID().toString().take(8).uppercase()

        val order = OrderEntity(
            orderNumber = randomOrderCode,
            productId = product.id,
            productTitle = product.title,
            productEmoji = product.emojiIcon,
            farmerId = product.farmerId,
            farmerName = product.farmerName,
            buyerId = buyer.id,
            buyerName = buyer.name,
            quantity = quantity,
            unit = product.unit,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            paymentStatus = "ESCROW_PAID",
            paymentMethod = paymentMethod,
            paymentTxnId = txnId,
            orderStatus = if (isQualityVerified) "QUALITY_VERIFIED" else "PLACED",
            trackingPartner = "Kisan Express Direct Logistics",
            trackingNumber = "KED-" + (100000..999999).random(),
            estimatedDeliveryDate = "Within 3 days",
            isQualityVerified = isQualityVerified,
            inspectionId = inspectionId
        )

        val orderId = dao.insertOrder(order)

        // Update product remaining quantity
        val remainingStock = maxOf(0.0, product.quantityAvailable - quantity)
        dao.updateProduct(product.copy(quantityAvailable = remainingStock))

        // Notify Farmer: New order & payment secured
        dao.insertNotification(
            NotificationEntity(
                userId = product.farmerId,
                role = "FARMER",
                title = "New Order Placed & Payment Secured! 💰",
                message = "${buyer.name} purchased $quantity ${product.unit} of '${product.title}'. Payment of ₹$totalAmount is secured in FarmPay Escrow.",
                type = "PAYMENT",
                referenceId = orderId
            )
        )

        // Notify Buyer: Order confirmation
        dao.insertNotification(
            NotificationEntity(
                userId = buyer.id,
                role = "BUYER",
                title = "Order Confirmed: #$randomOrderCode",
                message = "Payment of ₹$totalAmount received in Escrow. Farmer ${product.farmerName} will prepare and dispatch your fresh produce.",
                type = "ORDER",
                referenceId = orderId
            )
        )

        return orderId
    }

    suspend fun updateOrderStatus(order: OrderEntity, nextStatus: String) {
        val updated = order.copy(
            orderStatus = nextStatus,
            paymentStatus = if (nextStatus == "DELIVERED" || nextStatus == "COMPLETED") "RELEASED_TO_FARMER" else order.paymentStatus,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateOrder(updated)

        when (nextStatus) {
            "DISPATCHED" -> {
                dao.insertNotification(
                    NotificationEntity(
                        userId = order.buyerId,
                        role = "BUYER",
                        title = "Order Dispatched from Farm 🚚",
                        message = "Your order #${order.orderNumber} has been dispatched by ${order.farmerName} via ${order.trackingPartner} (Tracking: ${order.trackingNumber}).",
                        type = "DELIVERY",
                        referenceId = order.id
                    )
                )
            }
            "IN_TRANSIT" -> {
                dao.insertNotification(
                    NotificationEntity(
                        userId = order.buyerId,
                        role = "BUYER",
                        title = "Produce In Transit 🚛",
                        message = "Order #${order.orderNumber} is on the way to your delivery address. Expected: ${order.estimatedDeliveryDate}.",
                        type = "DELIVERY",
                        referenceId = order.id
                    )
                )
            }
            "DELIVERED" -> {
                // Escrow release notification
                dao.insertNotification(
                    NotificationEntity(
                        userId = order.farmerId,
                        role = "FARMER",
                        title = "Order Delivered & Escrow Released! 🌾💵",
                        message = "Order #${order.orderNumber} was marked delivered! ₹${order.totalAmount} has been released to your registered bank account.",
                        type = "PAYMENT",
                        referenceId = order.id
                    )
                )
                // Buyer rating prompt
                dao.insertNotification(
                    NotificationEntity(
                        userId = order.buyerId,
                        role = "BUYER",
                        title = "Produce Delivered! Rate Farmer ${order.farmerName} ⭐",
                        message = "Your delivery for #${order.orderNumber} is complete. Please rate the produce quality to build community trust!",
                        type = "RATING",
                        referenceId = order.id
                    )
                )
            }
        }
    }

    // --- Notifications ---
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(userId)
    fun getUnreadNotificationCount(userId: Long): Flow<Int> =
        dao.getUnreadNotificationCount(userId)
    suspend fun markAllNotificationsAsRead(userId: Long) =
        dao.markAllNotificationsAsRead(userId)

    // --- Ratings ---
    fun getRatingsForUser(userId: Long): Flow<List<RatingEntity>> =
        dao.getRatingsForUser(userId)
    val allRatings: Flow<List<RatingEntity>> = dao.getAllRatings()

    suspend fun submitRating(
        order: OrderEntity,
        fromUser: UserEntity,
        toUser: UserEntity,
        stars: Int,
        tags: String,
        comment: String
    ) {
        val rating = RatingEntity(
            orderId = order.id,
            fromUserId = fromUser.id,
            fromUserName = fromUser.name,
            fromRole = fromUser.role,
            toUserId = toUser.id,
            toUserName = toUser.name,
            stars = stars,
            tags = tags,
            comment = comment
        )
        dao.insertRating(rating)

        // Mark order as rated
        if (fromUser.role == "BUYER") {
            dao.updateOrder(order.copy(buyerRated = true))
        } else {
            dao.updateOrder(order.copy(farmerRated = true))
        }

        // Recalculate recipient rating
        val newCount = toUser.ratingCount + 1
        val newAvg = ((toUser.rating * toUser.ratingCount) + stars) / newCount
        dao.updateUser(toUser.copy(rating = newAvg, ratingCount = newCount))

        // Notify recipient
        dao.insertNotification(
            NotificationEntity(
                userId = toUser.id,
                role = toUser.role,
                title = "New Rating Received! ⭐",
                message = "${fromUser.name} rated you $stars stars: \"$comment\"",
                type = "RATING",
                referenceId = order.id
            )
        )
    }
}
