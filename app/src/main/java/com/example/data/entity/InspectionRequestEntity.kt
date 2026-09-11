package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspection_requests")
data class InspectionRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productTitle: String,
    val productEmoji: String = "🌾",
    val originalPricePerUnit: Double,
    val unit: String,
    val farmerId: Long,
    val farmerName: String,
    val buyerId: Long,
    val buyerName: String,
    val buyerPhone: String,
    val inspectionType: String, // "Farm Visit", "Mandi Meet", "Video Inspection"
    val preferredDate: String,
    val preferredTime: String,
    val meetingLocation: String,
    val buyerNotes: String = "",
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "VERIFIED_PASSED", "VERIFIED_REJECTED", "CANCELLED"
    val qualityGradeGiven: String = "", // e.g. "Grade A+ Certified Verified"
    val qualityVerificationNotes: String = "", // e.g. "Moisture 11%, zero pest damage, 100% natural aroma"
    val finalAgreedPrice: Double = 0.0,
    val requestedQuantity: Double = 1.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
