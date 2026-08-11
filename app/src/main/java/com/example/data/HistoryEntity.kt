package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calcId: String,
    val calcName: String,
    val category: String,
    val inputSummary: String,
    val resultSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
