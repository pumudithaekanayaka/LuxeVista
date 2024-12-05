package com.sn0w_w0lf.luxevista

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(applicationContext)
        setContent {
            MaterialTheme {
                HomeScreen(database)
            }
        }
    }
}

@Composable
fun HomeScreen(database: AppDatabase) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var promotions by remember { mutableStateOf<List<Promotion>>(emptyList()) }
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    val firstName = UserSession.firstName
    val lastName = UserSession.lastName

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            promotions = database.promotionDao().getAllPromotions()
            notifications = listOf(
                NotificationItem("New Room Available", "A new luxury room is available for booking."),
                NotificationItem("Special Promotion", "Book now and get 20% off!")
            )
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(onNavigateTo = { activityClass ->
                context.startActivity(Intent(context, activityClass))
            })
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background_image),
                    contentDescription = "Background Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(Color.Transparent),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Welcome, $firstName $lastName!",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            IconButton(
                                onClick = {
                                    val intent = Intent(context, NotificationActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.notification),
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        PromotionsSection(promotions)
                        NotificationsSection(notifications)
                    }
                }
            }
        }
    )
}

@Composable
fun PromotionsSection(promotions: List<Promotion>) {
    if (promotions.isEmpty()) {
        Text("No promotions available")
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(promotions.take(2)) { promotion -> // Limiting to 2 items
                PromotionItem(promotion)
            }
        }
    }
}

@Composable
fun PromotionItem(promotion: Promotion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Promotion: ${promotion.title}", style = MaterialTheme.typography.titleMedium)
            Text("Description: ${promotion.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NotificationsSection(notifications: List<NotificationItem>) {
    if (notifications.isEmpty()) {
        Text("No notifications available")
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(notifications.take(5)) { notification -> // Limiting to 5 notifications
                NotificationItemView(notification)
            }
        }
    }
}

@Composable
fun NotificationItemView(notification: NotificationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Notification: ${notification.title}", style = MaterialTheme.typography.titleMedium)
            Text("Details: ${notification.details}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class NotificationItem(val title: String, val details: String)

@Composable
fun BottomNavigationBar(
    onNavigateTo: (Class<*>) -> Unit,
    iconSize: Dp = 24.dp,
    labelFontSize: TextUnit = MaterialTheme.typography.labelSmall.fontSize
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { onNavigateTo(UserAccountActivity::class.java) },
            label = { Text("Users", fontSize = labelFontSize) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.portrait),
                    contentDescription = "Users",
                    modifier = Modifier.size(iconSize)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigateTo(RoomViewActivity::class.java) },
            label = { Text("Rooms", fontSize = labelFontSize) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bed),
                    contentDescription = "Rooms",
                    modifier = Modifier.size(iconSize)
                )
            }
        )
        NavigationBarItem(
            selected = true,
            onClick = { onNavigateTo(HomeActivity::class.java) },
            label = { Text("Home", fontSize = labelFontSize) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.house),
                    contentDescription = "Home",
                    modifier = Modifier.size(iconSize)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigateTo(AttractionsActivity::class.java) },
            label = { Text("Attractions", fontSize = labelFontSize) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.roller),
                    contentDescription = "Attractions",
                    modifier = Modifier.size(iconSize)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigateTo(PromotionActivity::class.java) },
            label = { Text("Promotions", fontSize = labelFontSize) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.megaphone),
                    contentDescription = "Promotions",
                    modifier = Modifier.size(iconSize)
                )
            }
        )
    }
}
