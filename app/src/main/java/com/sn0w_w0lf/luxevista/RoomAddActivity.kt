package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomAddActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "room-database"
        )
            .fallbackToDestructiveMigration()
            .build()

        ensureTablesExist()

        setContent {
            MaterialTheme {
                RoomAddScreen(database)
            }
        }
    }

    private fun ensureTablesExist() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.roomDao().getAllRooms()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RoomAddActivity,
                        "Error initializing database: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun RoomAddScreen(database: AppDatabase) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var roomName by remember { mutableStateOf("") }
    var roomCategory by remember { mutableStateOf("") }
    var roomPrice by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    var acFacility by remember { mutableStateOf(false) }
    var beds by remember { mutableStateOf("") }
    var wifiFacility by remember { mutableStateOf(false) }

    val backgroundImage: Painter = painterResource(id = R.drawable.background_image)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Card(
            modifier = Modifier
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = roomCategory,
                    onValueChange = { roomCategory = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = roomPrice,
                    onValueChange = { roomPrice = it },
                    label = { Text("Price (per night)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = beds,
                    onValueChange = { beds = it },
                    label = { Text("Number of Beds") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Checkbox(checked = acFacility, onCheckedChange = { acFacility = it })
                        Text("AC")
                    }
                    Row {
                        Checkbox(checked = wifiFacility, onCheckedChange = { wifiFacility = it })
                        Text("Wi-Fi")
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Availability:")
                    Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                }

                Button(
                    onClick = {
                        if (roomName.isNotBlank() && roomCategory.isNotBlank() && roomPrice.isNotBlank() && beds.isNotBlank()) {
                            scope.launch {
                                val room = ResortRoom(
                                    name = roomName,
                                    category = roomCategory,
                                    price = roomPrice.toInt(),
                                    isAvailable = isAvailable,
                                    ac = acFacility,
                                    wifi = wifiFacility,
                                    beds = beds.toInt()
                                )
                                database.roomDao().insert(room)
                                Toast.makeText(context, "Room added successfully!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill all required fields!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Room")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoomAddScreen() {
    val context = LocalContext.current
    val database = Room.databaseBuilder(context, AppDatabase::class.java, "room-database").build()

    MaterialTheme {
        RoomAddScreen(database)
    }
}
