package com.sn0w_w0lf.luxevista

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(room: ResortRoom)

    @Insert
    suspend fun insertAll(vararg rooms: ResortRoom)

    @Query("SELECT * FROM rooms")
    suspend fun getAllRooms(): List<ResortRoom>

    @Query("SELECT * FROM rooms WHERE category = :roomType")
    suspend fun getRoomsByType(roomType: String): List<ResortRoom>

    @Query("SELECT * FROM rooms WHERE isAvailable = 1")
    suspend fun getAvailableRooms(): List<ResortRoom>

    @Query("SELECT * FROM rooms WHERE LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%'")
    suspend fun searchRooms(searchQuery: String): List<ResortRoom>

    @Query("UPDATE rooms SET isAvailable = :isAvailable WHERE id = :roomId")
    suspend fun updateAvailability(roomId: Int, isAvailable: Boolean)

    @Update
    suspend fun updateRoom(room: ResortRoom)

    @Query("DELETE FROM rooms WHERE id = :roomId")
    suspend fun deleteRoomById(roomId: Int)

    @Delete
    suspend fun deleteRoom(room: ResortRoom)

    @Query("DELETE FROM rooms")
    suspend fun clearAllRooms()

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    suspend fun getRoomById(roomId: Int): ResortRoom
}
