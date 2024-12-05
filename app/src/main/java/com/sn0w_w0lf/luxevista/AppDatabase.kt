package com.sn0w_w0lf.luxevista

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        User::class,
        ResortRoom::class,
        InHouseService::class,
        Reservation::class,
        Attraction::class,
        BookingHistory::class,
        Promotion::class,
        UserService::class
    ],
    version = 5 // Incremented due to schema changes
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao
    abstract fun inHouseServiceDao(): InHouseServiceDao
    abstract fun reservationDao(): ReservationDao
    abstract fun attractionDao(): AttractionDao
    abstract fun bookingHistoryDao(): BookingHistoryDao
    abstract fun promotionDao(): PromotionDao
    abstract fun userServiceDao(): UserServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "luxevista-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
