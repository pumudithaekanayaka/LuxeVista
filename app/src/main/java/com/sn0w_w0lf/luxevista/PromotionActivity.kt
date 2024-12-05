package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromotionActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            PromotionScreen(database)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionScreen(database: AppDatabase) {
    val promotions = remember { mutableStateOf<List<Promotion>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            try {
                val fetchedPromotions = database.promotionDao().getAllPromotions()
                promotions.value = fetchedPromotions
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to fetch promotions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val backgroundImage: Painter = painterResource(id = R.drawable.background_image)
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Available Promotions") },
                        actions = {}
                    )
                },
                content = { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(promotions.value) { promotion ->
                            PromotionCard(promotion)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PromotionCard(promotion: Promotion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = promotion.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = promotion.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Discount: ${promotion.discountPercentage}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPromotionScreen() {
    val context = LocalContext.current
    val mockDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    LaunchedEffect(Unit) {
        mockDatabase.promotionDao().insertAll()
    }
    PromotionScreen(database = mockDatabase)
}

