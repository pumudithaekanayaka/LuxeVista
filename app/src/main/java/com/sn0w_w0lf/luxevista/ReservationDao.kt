package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReservationDao {
    @Insert
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM Reservation WHERE userId = :userId")
    suspend fun getUserReservations(userId: Int): List<Reservation>

    @Insert
    suspend fun insertAll(vararg reservations: Reservation)
}
