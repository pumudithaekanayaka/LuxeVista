package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PromotionDao {
    @Insert
    suspend fun insertPromotion(promotion: Promotion)

    @Query("SELECT * FROM Promotion")
    suspend fun getAllPromotions(): List<Promotion>

    @Insert
    suspend fun insertAll(vararg promotions: Promotion)
}
