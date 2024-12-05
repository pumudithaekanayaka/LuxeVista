package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BookingHistory")
data class BookingHistory(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val userId: Int,
    val roomId: Int,
    val roomName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val totalAmount: Double
)
