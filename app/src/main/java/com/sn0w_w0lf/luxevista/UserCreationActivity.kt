package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class UserCreationActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    companion object {
        private const val ADMIN_PASSCODE = "Admin123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "luxevista-database").build()

        setContent {
            MaterialTheme {
                UserCreationScreen(
                    onSubmit = { user ->
                        lifecycleScope.launch {
                            database.userDao().insert(user)
                            Toast.makeText(this@UserCreationActivity, "Account Created!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    },
                    adminPasscode = ADMIN_PASSCODE
                )
            }
        }
    }
}

@Composable
fun UserCreationScreen(onSubmit: (User) -> Unit, adminPasscode: String) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verifyPassword by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var adminCode by remember { mutableStateOf("") }

    val context = LocalContext.current

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = verifyPassword,
                        onValueChange = { verifyPassword = it },
                        label = { Text("Verify Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle between Admin and Guest
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Account Type: ")
                        Spacer(modifier = Modifier.width(8.dp))
                        ToggleButton(
                            checked = isAdmin,
                            onCheckedChange = { isAdmin = it },
                            labels = listOf("Guest", "Admin")
                        )
                    }

                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = adminCode,
                            onValueChange = { adminCode = it },
                            label = { Text("Admin Passcode") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (password == verifyPassword) {
                                if (isAdmin && adminCode != adminPasscode) {
                                    Toast.makeText(context, "Invalid Admin Passcode!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val user = User(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phone = phone,
                                    password = password,
                                    isAdmin = isAdmin
                                )
                                onSubmit(user)
                            } else {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Account", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    labels: List<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(labels[0], style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.padding(8.dp)
        )
        Text(labels[1], style = MaterialTheme.typography.bodyMedium)
    }
}
