package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attraction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val offer: String
)
