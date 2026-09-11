package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmerId: Long,
    val farmerName: String,
    val farmerPhone: String,
    val title: String,
    val category: String, // Grains, Vegetables, Fruits, Pulses, Dairy, Spices
    val description: String,
    val quantityAvailable: Double,
    val unit: String, // "kg", "quintal", "crate", "liter"
    val pricePerUnit: Double,
    val location: String,
    val qualityGrade: String, // "Grade A+ Organic", "Grade A Export", "Standard Farm Fresh"
    val harvestDate: String,
    val isOrganicCertified: Boolean = true,
    val allowsInspection: Boolean = true,
    val emojiIcon: String = "🌾",
    val status: String = "AVAILABLE" // "AVAILABLE", "SOLD_OUT"
)
