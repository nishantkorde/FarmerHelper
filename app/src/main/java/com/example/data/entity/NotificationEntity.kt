package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val role: String, // "FARMER" or "BUYER"
    val title: String,
    val message: String,
    val type: String, // "INSPECTION", "ORDER", "PAYMENT", "DELIVERY", "RATING"
    val referenceId: Long? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
