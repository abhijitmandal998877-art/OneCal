package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val calcId: String,
    val calcName: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
