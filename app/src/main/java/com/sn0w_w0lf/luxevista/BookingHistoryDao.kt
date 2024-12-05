package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingHistoryDao {
    @Insert
    suspend fun insertBooking(bookingHistory: BookingHistory)

    @Query("SELECT * FROM BookingHistory WHERE userId = :userId")
    suspend fun getBookingHistory(userId: Int): List<BookingHistory>

    @Insert
    suspend fun insertAll(vararg bookings: BookingHistory)

}
