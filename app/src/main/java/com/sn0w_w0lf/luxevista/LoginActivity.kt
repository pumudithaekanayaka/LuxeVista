package com.sn0w_w0lf.luxevista

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "luxevista-database").build()

        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            lifecycleScope.launch {
                DatabaseInitializer.initializeDatabase(applicationContext)
                with(sharedPreferences.edit()) {
                    putBoolean("isFirstRun", false)
                    apply()
                }
            }
        }

        setContent {
            MaterialTheme {
                LoginScreen(
                    onLogin = { email, password ->
                        lifecycleScope.launch {
                            val user = database.userDao().getUserByEmail(email)
                            if (user != null && user.password == password) {

                                UserSession.userId = user.id
                                UserSession.firstName = user.firstName
                                UserSession.lastName = user.lastName
                                UserSession.email = user.email
                                UserSession.phone = user.phone
                                UserSession.isAdmin = user.isAdmin

                                // Navigate to appropriate activity based on admin status
                                val intent = if (user.isAdmin) {
                                    Intent(this@LoginActivity, AdminActivity::class.java)
                                } else {
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onCreateAccount = {
                        val intent = Intent(this, UserCreationActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onCreateAccount: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image1),
            contentDescription = "Background Image",
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
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLogin(email, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onCreateAccount,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Account", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(
            onLogin = { _, _ -> },
            onCreateAccount = {}
        )
    }
}
