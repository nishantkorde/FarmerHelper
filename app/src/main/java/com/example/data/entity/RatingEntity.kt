package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val fromUserId: Long,
    val fromUserName: String,
    val fromRole: String, // "BUYER" or "FARMER"
    val toUserId: Long,
    val toUserName: String,
    val stars: Int, // 1 to 5
    val tags: String, // e.g. "Farm Fresh, Honest Weight, Prompt Dispatch"
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)
