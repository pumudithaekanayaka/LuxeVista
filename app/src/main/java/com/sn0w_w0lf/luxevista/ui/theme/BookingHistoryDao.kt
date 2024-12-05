package com.sn0w_w0lf.luxevista.ui.theme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sn0w_w0lf.luxevista.BookingHistory

@Dao
interface BookingHistoryDao {
    @Insert
    suspend fun insertBooking(bookingHistory: BookingHistory)

    @Query("SELECT * FROM BookingHistory WHERE userId = :userId")
    suspend fun getBookingHistory(userId: Int): List<BookingHistory>
}
