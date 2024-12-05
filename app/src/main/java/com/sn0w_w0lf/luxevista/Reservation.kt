package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val serviceId: String,
    val date: Long,
    val time: String
)
