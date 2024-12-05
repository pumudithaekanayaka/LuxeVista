package com.sn0w_w0lf.luxevista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NotificationScreen()
            }
        }
    }
}

@Composable
fun NotificationScreen() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "New Notifications",
            style = MaterialTheme.typography.headlineLarge,
        )

        // Here you can add a list of notifications if needed
        Text(text = "You have a new promotion available!")
        Text(text = "A new room is available for booking!")
    }
}
