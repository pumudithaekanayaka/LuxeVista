package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserService(userService: UserService)

    @Query("SELECT * FROM in_house_services INNER JOIN user_services ON in_house_services.id = user_services.serviceId WHERE user_services.userId = :userId")
    suspend fun getServicesForUser(userId: Int): List<InHouseService>
}
