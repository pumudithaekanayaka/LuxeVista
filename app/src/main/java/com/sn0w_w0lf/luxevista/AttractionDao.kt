package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AttractionDao {
    @Query("SELECT * FROM Attraction")
    suspend fun getAllAttractions(): List<Attraction>

   @Insert
    suspend fun insertAttraction(attraction: Attraction)

    @Insert
    suspend fun insertAll(vararg attractions: Attraction)
}
