package com.sn0w_w0lf.luxevista

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseInitializer {

    companion object {

        @OptIn(DelicateCoroutinesApi::class)
        suspend fun initializeDatabase(context: Context) {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "luxevista-database"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        GlobalScope.launch(Dispatchers.IO) {
                            populateDatabase(context)
                        }
                    }
                })
                .build()

            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                with(sharedPreferences.edit()) {
                    putBoolean("isFirstRun", false)
                    apply()
                }

                populateDatabase(context)
            }
        }

        private suspend fun populateDatabase(context: Context) {
            withContext(Dispatchers.IO) {
                val database = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "luxevista-database"
                ).build()

                val userDao = database.userDao()
                val roomDao = database.roomDao()
                val inHouseServiceDao = database.inHouseServiceDao()
                val reservationDao = database.reservationDao()
                val attractionDao = database.attractionDao()
                val bookingHistoryDao = database.bookingHistoryDao()
                val promotionDao = database.promotionDao()

                // Insert Users
                userDao.insertAll(
                    User(firstName = "John", lastName = "Doe", email = "john.doe@example.com", phone = "1234567890", password = "password123", isAdmin = true),
                    User(firstName = "Jane", lastName = "Smith", email = "jane.smith@example.com", phone = "0987654321", password = "securePass!", isAdmin = false),
                    User(firstName = "Alice", lastName = "Johnson", email = "alice.johnson@example.com", phone = "1112223333", password = "alice@123", isAdmin = false),
                    User(firstName = "Bob", lastName = "Brown", email = "bob.brown@example.com", phone = "4445556666", password = "password456", isAdmin = false),
                    User(firstName = "Emma", lastName = "Wilson", email = "emma.wilson@example.com", phone = "7778889999", password = "emmaSecure", isAdmin = true),
                    User(firstName = "Liam", lastName = "Taylor", email = "liam.taylor@example.com", phone = "1231231234", password = "Liam123!", isAdmin = false),
                    User(firstName = "Sophia", lastName = "Martinez", email = "sophia.martinez@example.com", phone = "9879879876", password = "SophiaPW", isAdmin = false),
                    User(firstName = "Ethan", lastName = "Anderson", email = "ethan.anderson@example.com", phone = "5556667777", password = "Ethan@456", isAdmin = true),
                    User(firstName = "Mia", lastName = "Thomas", email = "mia.thomas@example.com", phone = "1111111111", password = "MiaSecure!", isAdmin = false),
                    User(firstName = "Lucas", lastName = "Jackson", email = "lucas.jackson@example.com", phone = "2222222222", password = "Lucas123", isAdmin = true),
                    User(firstName = "Olivia", lastName = "White", email = "olivia.white@example.com", phone = "3333333333", password = "OliviaPW!", isAdmin = false),
                    User(firstName = "Noah", lastName = "Harris", email = "noah.harris@example.com", phone = "4444444444", password = "NoahPass", isAdmin = true),
                    User(firstName = "Ava", lastName = "Clark", email = "ava.clark@example.com", phone = "5555555555", password = "AvaSecure", isAdmin = false),
                    User(firstName = "William", lastName = "Lewis", email = "william.lewis@example.com", phone = "6666666666", password = "WilliamPW", isAdmin = true),
                    User(firstName = "Isabella", lastName = "Robinson", email = "isabella.robinson@example.com", phone = "7777777777", password = "Isabella123", isAdmin = false)
                )

                // Insert Resort Rooms
                roomDao.insertAll(
                    ResortRoom(name = "Ocean View Suite", category = "Deluxe", price = 150, isAvailable = true, ac = true, wifi = true, beds = 2),
                    ResortRoom(name = "Garden View Room", category = "Standard", price = 100, isAvailable = true, ac = false, wifi = true, beds = 1),
                    ResortRoom(name = "Mountain View Suite", category = "Luxury", price = 200, isAvailable = true, ac = true, wifi = true, beds = 3),
                    ResortRoom(name = "Poolside Room", category = "Standard", price = 120, isAvailable = true, ac = true, wifi = true, beds = 2),
                    ResortRoom(name = "Seaside Cottage", category = "Luxury", price = 250, isAvailable = true, ac = true, wifi = true, beds = 4),
                    ResortRoom(name = "Hillside Lodge", category = "Deluxe", price = 180, isAvailable = true, ac = true, wifi = true, beds = 2),
                    ResortRoom(name = "Forest Retreat", category = "Standard", price = 110, isAvailable = true, ac = false, wifi = true, beds = 1),
                    ResortRoom(name = "Luxury Penthouse", category = "Luxury", price = 350, isAvailable = true, ac = true, wifi = true, beds = 4),
                    ResortRoom(name = "City View Room", category = "Standard", price = 130, isAvailable = true, ac = true, wifi = false, beds = 2),
                    ResortRoom(name = "Beachfront Villa", category = "Luxury", price = 400, isAvailable = true, ac = true, wifi = true, beds = 5)
                )

                // Insert In-House Services
                inHouseServiceDao.insertAll(
                    InHouseService(name = "Spa Treatment", description = "Relax and rejuvenate with our premium spa services.", price = 50, isAvailable = true),
                    InHouseService(name = "Room Service", description = "24/7 room service for all your needs.", price = 20, isAvailable = true),
                    InHouseService(name = "Laundry Service", description = "Get your clothes cleaned and pressed.", price = 15, isAvailable = true),
                    InHouseService(name = "Private Chef", description = "Hire a private chef for a gourmet experience.", price = 150, isAvailable = true),
                    InHouseService(name = "Guided Tours", description = "Explore the local area with a private guide.", price = 100, isAvailable = true),
                    InHouseService(name = "Airport Shuttle", description = "Convenient transport to and from the airport.", price = 30, isAvailable = true),
                    InHouseService(name = "Fitness Classes", description = "Daily yoga and fitness sessions.", price = 25, isAvailable = true),
                    InHouseService(name = "Bar Service", description = "Enjoy drinks and snacks by the pool.", price = 10, isAvailable = true),
                    InHouseService(name = "Babysitting", description = "Childcare services for your convenience.", price = 40, isAvailable = true),
                    InHouseService(name = "Personal Shopping", description = "Personalized shopping experience with a local guide.", price = 120, isAvailable = true)
                )

                // Insert Reservations
                reservationDao.insertAll(
                    Reservation(userId = 1, serviceId = "1", date = System.currentTimeMillis(), time = "10:00 AM"),
                    Reservation(userId = 2, serviceId = "2", date = System.currentTimeMillis() + 86400000, time = "3:00 PM"),
                    Reservation(userId = 3, serviceId = "3", date = System.currentTimeMillis() + 172800000, time = "5:00 PM"),
                    Reservation(userId = 4, serviceId = "4", date = System.currentTimeMillis() + 259200000, time = "2:00 PM"),
                    Reservation(userId = 5, serviceId = "5", date = System.currentTimeMillis() + 345600000, time = "11:00 AM"),
                    Reservation(userId = 6, serviceId = "6", date = System.currentTimeMillis() + 432000000, time = "9:00 AM"),
                    Reservation(userId = 7, serviceId = "7", date = System.currentTimeMillis() + 518400000, time = "1:00 PM"),
                    Reservation(userId = 8, serviceId = "8", date = System.currentTimeMillis() + 604800000, time = "12:00 PM"),
                    Reservation(userId = 9, serviceId = "9", date = System.currentTimeMillis() + 691200000, time = "4:00 PM"),
                    Reservation(userId = 10, serviceId = "10", date = System.currentTimeMillis() + 777600000, time = "6:00 PM")
                )

                // Insert Attractions
                attractionDao.insertAll(
                    Attraction(id = 1, name = "Sunny Beach", description = "Relax by the shore.", offer = "Free sunbeds on weekdays"),
                    Attraction(id = 2, name = "Mountain View Trek", description = "Explore breathtaking trails.", offer = "20% off for group bookings"),
                    Attraction(id = 3, name = "City Skyline Tour", description = "See the city's best views.", offer = "Free drink with each ticket"),
                    Attraction(id = 4, name = "Historic Museum", description = "Learn about local history.", offer = "Free entry on weekends"),
                    Attraction(id = 5, name = "Adventure Park", description = "Thrilling rides for all ages.", offer = "Buy one, get one free tickets"),
                    Attraction(id = 6, name = "Botanical Garden", description = "Explore lush gardens.", offer = "10% off on membership"),
                    Attraction(id = 7, name = "River Cruise", description = "Enjoy a scenic boat ride.", offer = "Free drink on board"),
                    Attraction(id = 8, name = "Cultural Show", description = "Watch local performances.", offer = "Discounts for group bookings"),
                    Attraction(id = 9, name = "Luxury Spa", description = "Indulge in a pampering experience.", offer = "Free spa consultation"),
                    Attraction(id = 10, name = "Golf Course", description = "Tee off at the premium golf course.", offer = "Discounts for members")
                )

                // Insert Booking History
                bookingHistoryDao.insertAll(
                    BookingHistory(userId = 1, roomId = 1, roomName = "Ocean View Suite", checkInDate = "2024-12-01", checkOutDate = "2024-12-05", totalAmount = 750.0),
                    BookingHistory(userId = 2, roomId = 2, roomName = "Garden View Room", checkInDate = "2024-12-10", checkOutDate = "2024-12-12", totalAmount = 240.0),
                    BookingHistory(userId = 3, roomId = 3, roomName = "Mountain View Suite", checkInDate = "2024-12-15", checkOutDate = "2024-12-20", totalAmount = 1000.0),
                    BookingHistory(userId = 4, roomId = 4, roomName = "Poolside Room", checkInDate = "2024-12-01", checkOutDate = "2024-12-03", totalAmount = 360.0),
                    BookingHistory(userId = 5, roomId = 5, roomName = "Seaside Cottage", checkInDate = "2024-12-20", checkOutDate = "2024-12-25", totalAmount = 1250.0),
                    BookingHistory(userId = 6, roomId = 6, roomName = "Hillside Lodge", checkInDate = "2024-12-25", checkOutDate = "2024-12-30", totalAmount = 900.0),
                    BookingHistory(userId = 7, roomId = 7, roomName = "Forest Retreat", checkInDate = "2024-12-05", checkOutDate = "2024-12-10", totalAmount = 550.0),
                    BookingHistory(userId = 8, roomId = 8, roomName = "Luxury Penthouse", checkInDate = "2024-12-12", checkOutDate = "2024-12-18", totalAmount = 2100.0),
                    BookingHistory(userId = 9, roomId = 9, roomName = "City View Room", checkInDate = "2024-12-22", checkOutDate = "2024-12-24", totalAmount = 260.0),
                    BookingHistory(userId = 10, roomId = 10, roomName = "Beachfront Villa", checkInDate = "2024-12-01", checkOutDate = "2024-12-05", totalAmount = 2000.0)
                )

                // Insert Promotions
                promotionDao.insertAll(
                    Promotion(title = "Winter Sale", description = "Get 50% off on selected items!", discountPercentage = 50),
                    Promotion(title = "Summer Collection", description = "New arrivals for the summer season!", discountPercentage = 20),
                    Promotion(title = "Black Friday Deals", description = "Hurry up, grab the best deals of the year!", discountPercentage = 30),
                    Promotion(title = "Holiday Discounts", description = "Exclusive holiday discounts for early bookings.", discountPercentage = 25),
                    Promotion(title = "Early Bird Offer", description = "Book in advance and save up to 30%!", discountPercentage = 30),
                    Promotion(title = "VIP Access", description = "Sign up for VIP status and get exclusive offers!", discountPercentage = 15),
                    Promotion(title = "Family Package", description = "Family getaway deals, kids stay free!", discountPercentage = 40),
                    Promotion(title = "Luxury Package", description = "Indulge in our luxury rooms with a 25% discount.", discountPercentage = 25),
                    Promotion(title = "Weekend Getaway", description = "Enjoy a weekend break with 15% off your stay.", discountPercentage = 15),
                    Promotion(title = "Spa Retreat", description = "Free spa treatment for bookings over 5 nights.", discountPercentage = 20),
                    Promotion(title = "Group Discount", description = "Book for 4 or more and get a 20% discount!", discountPercentage = 20),
                    Promotion(title = "Student Offer", description = "Student discount on all reservations during the summer.", discountPercentage = 10),
                    Promotion(title = "Couple's Special", description = "Special deals for romantic getaways.", discountPercentage = 35),
                    Promotion(title = "Seasonal Clearance", description = "Last chance to grab seasonal items at discounted prices.", discountPercentage = 40),
                    Promotion(title = "Anniversary Sale", description = "Celebrating our anniversary with huge discounts!", discountPercentage = 50)
                )
            }
        }
    }
}
