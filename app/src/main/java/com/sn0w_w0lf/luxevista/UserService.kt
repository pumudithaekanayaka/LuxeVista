package com.sn0w_w0lf.luxevista

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_services",
    primaryKeys = ["userId", "serviceId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = InHouseService::class, parentColumns = ["id"], childColumns = ["serviceId"])
    ]
)
data class UserService(
    val userId: Int,
    val serviceId: Int
)
