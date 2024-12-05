package com.sn0w_w0lf.luxevista

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class UserViewActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                UserListScreen(database)
            }
        }
    }
}

@Composable
fun UserListScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            users = database.userDao().getAllUsers()
        }
    }

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
            Text(
                text = "Users List",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (users.isEmpty()) {
                Text(
                    text = "No users found.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            onEditClick = {
                            },
                            onDeleteClick = {
                                scope.launch {
                                    database.userDao().deleteUser(user)
                                    users = database.userDao().getAllUsers()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Phone: ${user.phone}", style = MaterialTheme.typography.bodyMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    val intent = Intent(context, UserEditActivity::class.java).apply {
                        putExtra("userId", user.id)
                        putExtra("firstName", user.firstName)
                        putExtra("lastName", user.lastName)
                        putExtra("email", user.email)
                        putExtra("phone", user.phone)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Edit")
                }

                Button(onClick = {
                    onDeleteClick()
                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen() {
    val context = LocalContext.current
    val mockDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    LaunchedEffect(Unit) {
        mockDatabase.userDao().insertAll()
    }
    MaterialTheme {
        UserListScreen(database = mockDatabase)
    }
}
