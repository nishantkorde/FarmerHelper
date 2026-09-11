package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val productId: Long,
    val productTitle: String,
    val productEmoji: String = "🌾",
    val farmerId: Long,
    val farmerName: String,
    val buyerId: Long,
    val buyerName: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val totalAmount: Double,
    val deliveryAddress: String,
    val paymentStatus: String = "ESCROW_PAID", // "ESCROW_PAID", "RELEASED_TO_FARMER", "REFUNDED"
    val paymentMethod: String = "Escrow FarmPay (UPI/Card)",
    val paymentTxnId: String,
    val orderStatus: String = "PLACED", // "PLACED", "QUALITY_VERIFIED", "DISPATCHED", "IN_TRANSIT", "DELIVERED", "COMPLETED"
    val trackingPartner: String = "AgriDirect Rural Freight",
    val trackingNumber: String = "",
    val estimatedDeliveryDate: String = "",
    val isQualityVerified: Boolean = false,
    val inspectionId: Long? = null,
    val buyerRated: Boolean = false,
    val farmerRated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
