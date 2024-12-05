package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.room.Room
import com.sn0w_w0lf.luxevista.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.*

class UserAccountActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        val userId = UserSession.userId

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            MaterialTheme {
                UserAccountScreen(
                    userId = userId,
                    bookingHistoryDao = database.bookingHistoryDao(),
                    reservationDao = database.reservationDao()
                )
            }
        }
    }
}

@Composable
fun UserAccountScreen(
    userId: Int,
    bookingHistoryDao: BookingHistoryDao,
    reservationDao: ReservationDao
) {
    val firstName = UserSession.firstName ?: "Guest"
    val lastName = UserSession.lastName ?: ""
    var bookingHistory by remember { mutableStateOf<List<BookingHistory>>(emptyList()) }
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    val selectedDate = remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(userId) {
        scope.launch {
            val bookings = bookingHistoryDao.getBookingHistory(userId)
            val reservationList = reservationDao.getUserReservations(userId)

            bookingHistory = bookings
            reservations = reservationList
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                UserNameCard(firstName, lastName)

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CalendarCard(selectedDate)

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Booking History Section
                Text("Booking History:")
                if (bookingHistory.isNotEmpty()) {
                    bookingHistory.forEach { booking ->
                        BookingCard(booking)
                    }
                } else {
                    Text("No bookings found.")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Reservations Section
                Text("Reservations:")
                if (reservations.isNotEmpty()) {
                    reservations.forEach { reservation ->
                        ReservationCard(reservation)
                    }
                } else {
                    Text("No reservations found.")
                }
            }
        }
    }
}


@Composable
fun UserNameCard(firstName: String, lastName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Welcome, $firstName $lastName!")
        }
    }
}

@Composable
fun CalendarCard(selectedDate: MutableState<Long?>) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Travel Dates:")

            AndroidView(factory = {
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(year, month, dayOfMonth)
                        selectedDate.value = selectedCalendar.timeInMillis
                    }
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(300.dp))

            selectedDate.value?.let {
                Text("Selected Date: ${Date(it)}")
            }
        }
    }
}

@Composable
fun BookingAndReservationCard(
    bookingHistory: List<BookingHistory>,
    reservations: List<Reservation>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Booking History:")
            if (bookingHistory.isNotEmpty()) {
                LazyColumn {
                    items(bookingHistory) { booking ->
                        BookingCard(booking)
                    }
                }
            } else {
                Text("No bookings found.")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Reservations:")
            if (reservations.isNotEmpty()) {
                LazyColumn {
                    items(reservations) { reservation ->
                        ReservationCard(reservation)
                    }
                }
            } else {
                Text("No reservations found.")
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Booking ID: ${booking.bookingId}")
            Text("Room: ${booking.roomName}")
            Text("Check-in: ${booking.checkInDate}")
            Text("Check-out: ${booking.checkOutDate}")
            Text("Total Amount: \$${booking.totalAmount}")
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation) {
    // Map service ids to names
    val serviceNames = mapOf(
        1 to "Spa Treatments",
        2 to "Fine Dining",
        3 to "Poolside Cabanas",
        4 to "Guided Beach Tours"
    )

    // Convert the serviceId into a list of service names
    val selectedServices = reservation.serviceId.split(",").mapNotNull { serviceNames[it.toInt()] }

    // Format the date into a readable string
    val formattedDate = if (reservation.date is Long) {
        // Directly format the Long timestamp as a date string
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(reservation.date))
    } else {
        // Assuming reservation.date is a string in "yyyy-MM-dd" format
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = dateFormat.parse(reservation.date) // Parse if it's a string
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate ?: Date()) // Format to a string
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reservation ID: ${reservation.id}")
            Text("Services: ${selectedServices.joinToString(", ")}")
            Text("Date: $formattedDate") // Show the formatted date
            Text("Time: ${reservation.time}")
        }
    }
}

private fun SimpleDateFormat.parse(lng: kotlin.Long) {}


fun String.toDateMillis(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.parse(this)?.time ?: 0L
}
