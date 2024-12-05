package com.sn0w_w0lf.luxevista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

class AttractionsActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                AttractionsScreen(database)
            }
        }
    }
}

@Composable
fun AttractionsScreen(database: AppDatabase) {
    val attractions = remember { mutableStateListOf<Attraction>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val fetchedAttractions = database.attractionDao().getAllAttractions()
                attractions.addAll(fetchedAttractions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nearby Attractions",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            if (attractions.isEmpty()) {
                Text("No attractions available", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(attractions) { attraction ->
                        AttractionCard(attraction)
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionCard(attraction: Attraction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = attraction.name, style = MaterialTheme.typography.titleMedium)
            Text(text = attraction.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Exclusive Offer: ${attraction.offer}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAttractionsScreen() {
    val context = LocalContext.current
    val mockDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    MaterialTheme {
        AttractionsScreen(database = mockDatabase)
    }
}

