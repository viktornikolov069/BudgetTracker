package com.example.budgettracker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "transaction_label")
    val label: String,
    @ColumnInfo(name = "transaction_amount")
    val amount: Double,
    @ColumnInfo(name = "transaction_description")
    val description: String) {
}