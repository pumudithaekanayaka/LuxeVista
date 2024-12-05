package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ServiceBookingActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                ServiceBookingScreen(database)
            }
        }
    }
}

@Composable
fun ServiceBookingScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val userId = UserSession.userId ?: 0
    val userFirstName = UserSession.firstName.orEmpty()
    val userLastName = UserSession.lastName.orEmpty()
    val userEmail = UserSession.email.orEmpty()

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    var spaTreatmentSelected by remember { mutableStateOf(false) }
    var fineDiningSelected by remember { mutableStateOf(false) }
    var poolsideCabanasSelected by remember { mutableStateOf(false) }
    var beachToursSelected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Welcome, $userFirstName $userLastName")
                    Text("Email: $userEmail")

                    // Date Picker Button
                    DatePicker(
                        onDateSelected = { timestamp ->
                            selectedDate = timestamp
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Time Picker Button
                    TimePicker(
                        onTimeSelected = { time ->
                            selectedTime = time
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Service Toggles
                    ServiceToggle(title = "Spa Treatments", isSelected = spaTreatmentSelected) {
                        spaTreatmentSelected = it
                    }
                    ServiceToggle(title = "Fine Dining", isSelected = fineDiningSelected) {
                        fineDiningSelected = it
                    }
                    ServiceToggle(title = "Poolside Cabanas", isSelected = poolsideCabanasSelected) {
                        poolsideCabanasSelected = it
                    }
                    ServiceToggle(title = "Guided Beach Tours", isSelected = beachToursSelected) {
                        beachToursSelected = it
                    }

                    // Booking button
                    Button(
                        onClick = {
                            if (selectedDate != null && selectedTime.isNotBlank()) {
                                val selectedServices = mutableListOf<Int>()
                                if (spaTreatmentSelected) selectedServices.add(1)
                                if (fineDiningSelected) selectedServices.add(2)
                                if (poolsideCabanasSelected) selectedServices.add(3)
                                if (beachToursSelected) selectedServices.add(4)

                                scope.launch {
                                    database.reservationDao().insertReservation(
                                        Reservation(
                                            userId = userId,
                                            serviceId = selectedServices.joinToString(","),
                                            date = selectedDate!!, // Pass the timestamp (Long)
                                            time = selectedTime
                                        )
                                    )
                                    Toast.makeText(context, "Service booked successfully!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Book Service")
                    }
                }
            }
        }
    }
}

@Composable
fun TimePicker(onTimeSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Button(
        onClick = {
            val timePicker = android.app.TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val time = String.format("%02d:%02d", hourOfDay, minute)
                    onTimeSelected(time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        },
        modifier = modifier
    ) {
        Text("Select Time")
    }
}

@Composable
fun ServiceToggle(title: String, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = isSelected, onCheckedChange = { onToggle(it) })
    }
}

@Composable
fun DatePicker(
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf("") }

    Button(
        onClick = {
            val datePicker = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                    selectedDate = formattedDate

                    val timestamp = calendar.timeInMillis
                    onDateSelected(timestamp)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        },
        modifier = modifier
    ) {
        Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewServiceBookingScreen() {
    MaterialTheme {
        val mockDatabase = AppDatabase.getDatabase(LocalContext.current)
        ServiceBookingScreen(mockDatabase)
    }
}
