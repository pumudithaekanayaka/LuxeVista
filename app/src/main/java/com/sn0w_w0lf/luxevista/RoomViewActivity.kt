package com.sn0w_w0lf.luxevista

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.room.Room

class RoomViewActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                RoomListScreen(database) { selectedRoom ->
                    val intent = Intent(this, RoomBookingActivity::class.java)
                    intent.putExtra("roomId", selectedRoom.id)
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun RoomListScreen(database: AppDatabase, onRoomSelected: (ResortRoom) -> Unit) {
    val scope = rememberCoroutineScope()
    var availableRooms by remember { mutableStateOf<List<ResortRoom>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            availableRooms = database.roomDao().getAvailableRooms()
        }
    }
    val backgroundImage = painterResource(id = R.drawable.background_image)

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Available Rooms",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (availableRooms.isEmpty()) {
                Text(
                    text = "No rooms available at the moment.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(availableRooms) { room ->
                        RoomItem(room, onRoomSelected)
                    }
                }
            }
        }
    }
}


@Composable
fun RoomItem(room: ResortRoom, onRoomSelected: (ResortRoom) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onRoomSelected(room) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = room.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Category: ${room.category}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price: $${room.price} per night",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Beds: ${room.beds}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (room.ac) Text("AC", style = MaterialTheme.typography.bodySmall)
                if (room.wifi) Text("Wi-Fi", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = if (room.isAvailable) "Available" else "Not Available",
                style = MaterialTheme.typography.bodyMedium,
                color = if (room.isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoomListScreen() {
    val context = LocalContext.current
    val mockDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    MaterialTheme {
        Column {
            Text(
                text = "Available Rooms",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
            }
        }
        LaunchedEffect(Unit) {
            mockDatabase.roomDao().insertAll()
        }
    }
}
