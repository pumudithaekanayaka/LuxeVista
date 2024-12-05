package com.sn0w_w0lf.luxevista

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MaterialTheme {
                AdminScreen()
            }
        }
    }
}

@Composable
fun AdminScreen() {

    val context = LocalContext.current
    val firstName = UserSession.firstName
    val lastName = UserSession.lastName

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Admin Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Admin Panel")
            Text("Welcome, Admin $firstName $lastName!")

            AdminButton("Create Attraction") {
                context.startActivity(Intent(context, CreateAttractionActivity::class.java))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdminButton("Add Room") {
                context.startActivity(Intent(context, RoomAddActivity::class.java))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdminButton("View Users") {
                context.startActivity(Intent(context, UserViewActivity::class.java))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdminButton("Add Promotion") {
                context.startActivity(Intent(context, AddPromotionActivity::class.java))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdminButton("Home") {
                context.startActivity(Intent(context, HomeActivity::class.java))
            }
        }
    }
}

@Composable
fun AdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminScreen() {
    AdminScreen()
}
