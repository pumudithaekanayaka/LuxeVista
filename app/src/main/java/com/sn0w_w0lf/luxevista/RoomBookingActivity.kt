package com.sn0w_w0lf.luxevista

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RoomBookingActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        val roomId = intent.getIntExtra("roomId", 0)

        CoroutineScope(Dispatchers.Main).launch {
            val selectedRoom = getRoomById(roomId)
            setContent {
                MaterialTheme {
                    RoomBookingScreen(selectedRoom, database)
                }
            }
        }
    }

    private suspend fun getRoomById(roomId: Int): ResortRoom {
        return database.roomDao().getRoomById(roomId)
    }
}

@Composable
fun RoomBookingScreen(room: ResortRoom, database: AppDatabase) {
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var guestFirstName by remember { mutableStateOf(UserSession.firstName ?: "") }
    var guestLastName by remember { mutableStateOf(UserSession.lastName ?: "") }
    var guestEmail by remember { mutableStateOf(UserSession.email ?: "") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    fun showDatePicker(isCheckIn: Boolean) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formattedDate = dateFormat.format(selectedDate.time)
                if (isCheckIn) {
                    checkInDate = formattedDate
                } else {
                    checkOutDate = formattedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun calculateTotalAmount(pricePerNight: Double, checkIn: String, checkOut: String): Double {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val checkInDate = sdf.parse(checkIn)
            val checkOutDate = sdf.parse(checkOut)
            val diff = checkOutDate.time - checkInDate.time
            val days = (diff / (1000 * 60 * 60 * 24)).toInt()
            days * pricePerNight
        } catch (e: Exception) {
            0.0
        }
    }

    fun saveBooking() {
        CoroutineScope(Dispatchers.IO).launch {
            val booking = BookingHistory(
                userId = UserSession.userId ?: 0,
                roomId = room.id,
                roomName = room.name,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                totalAmount = calculateTotalAmount(room.price.toDouble(), checkInDate, checkOutDate)
            )
            database.bookingHistoryDao().insertBooking(booking)
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Booking saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Background image
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Booking Room: ${room.name}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Category: ${room.category}")
                    Text("Price: $${room.price} per night")
                    Text("Beds: ${room.beds}")
                    Row {
                        if (room.ac) Text("AC ", style = MaterialTheme.typography.bodySmall)
                        if (room.wifi) Text(" Wi-Fi", style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = guestFirstName,
                        onValueChange = { guestFirstName = it },
                        label = { Text("Guest First Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                    OutlinedTextField(
                        value = guestLastName,
                        onValueChange = { guestLastName = it },
                        label = { Text("Guest Last Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                    OutlinedTextField(
                        value = guestEmail,
                        onValueChange = { guestEmail = it },
                        label = { Text("Guest Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                    Text("checkInDate")
                    Button(
                        onClick = { showDatePicker(true) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(checkInDate)
                    }
                    Text("checkOutDate")
                    Button(
                        onClick = { showDatePicker(false) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(checkOutDate)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, ServiceBookingActivity::class.java).apply {
                                putExtra("roomId", room.id)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Service Booking")
                    }

                    Button(
                        onClick = {
                            if (guestFirstName.isNotBlank() && guestLastName.isNotBlank() && guestEmail.isNotBlank() && checkInDate.isNotBlank() && checkOutDate.isNotBlank()) {
                                saveBooking()
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}
