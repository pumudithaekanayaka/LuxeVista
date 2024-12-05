package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "in_house_services")
data class InHouseService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Int,
    val isAvailable: Boolean = true
)
