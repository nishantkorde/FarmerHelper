package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val name: String,
    val role: String, // "FARMER" or "BUYER"
    val phone: String,
    val location: String,
    val farmName: String = "",
    val rating: Float = 5.0f,
    val ratingCount: Int = 1,
    val isVerified: Boolean = true,
    val avatarInitials: String = ""
)
