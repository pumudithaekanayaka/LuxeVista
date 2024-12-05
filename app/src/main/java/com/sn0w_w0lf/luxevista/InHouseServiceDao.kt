package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface InHouseServiceDao {

    @Insert
    suspend fun insertService(service: InHouseService): Long

    @Query("SELECT * FROM in_house_services WHERE isAvailable = 1")
    suspend fun getAvailableServices(): List<InHouseService>

    @Query("SELECT * FROM in_house_services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: Int): InHouseService

    @Update
    suspend fun updateService(service: InHouseService)

    @Delete
    suspend fun deleteService(service: InHouseService)

    @Insert
    suspend fun insertAll(vararg services: InHouseService)
}
