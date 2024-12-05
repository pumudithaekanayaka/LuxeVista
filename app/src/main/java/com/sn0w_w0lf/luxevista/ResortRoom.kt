package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class ResortRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val price: Int,
    val isAvailable: Boolean,
    val ac: Boolean,
    val wifi: Boolean,
    val beds: Int
)
