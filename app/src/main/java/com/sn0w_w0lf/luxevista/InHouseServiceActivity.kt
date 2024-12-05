package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

class InHouseServiceActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                InHouseServiceScreen(database)
            }
        }
    }
}

@Composable
fun InHouseServiceScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var serviceName by remember { mutableStateOf("") }
    var serviceDescription by remember { mutableStateOf("") }
    var servicePrice by remember { mutableStateOf("") }
    var isServiceAvailable by remember { mutableStateOf(true) }
    var availableServices by remember { mutableStateOf<List<InHouseService>>(emptyList()) }

    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Fetch available services and users
    LaunchedEffect(Unit) {
        availableServices = database.inHouseServiceDao().getAvailableServices()
        users = database.userDao().getAllUsers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Service Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serviceDescription,
                    onValueChange = { serviceDescription = it },
                    label = { Text("Service Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = servicePrice,
                    onValueChange = { servicePrice = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Service Available:")
                    Switch(checked = isServiceAvailable, onCheckedChange = { isServiceAvailable = it })
                }
                Text("Select User:")
                Box {
                    Button(
                        onClick = { isDropdownExpanded = !isDropdownExpanded },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedUserId?.let { userId ->
                            users.find { it.id == userId }?.let { "${it.firstName} ${it.lastName}" }
                        } ?: "Select a User")
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        users.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.firstName} ${user.lastName}") },
                                onClick = {
                                    selectedUserId = user.id
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedUserId != null && serviceName.isNotBlank() && serviceDescription.isNotBlank() && servicePrice.isNotBlank()) {
                            val price = servicePrice.toIntOrNull()
                            if (price != null) {
                                scope.launch {
                                    val newService = InHouseService(
                                        name = serviceName,
                                        description = serviceDescription,
                                        price = price,
                                        isAvailable = isServiceAvailable
                                    )
                                    val serviceId = database.inHouseServiceDao().insertService(newService).toInt()

                                    val userService = UserService(userId = selectedUserId!!, serviceId = serviceId)
                                    database.userServiceDao().insertUserService(userService)

                                    availableServices = database.inHouseServiceDao().getAvailableServices()

                                    Toast.makeText(context, "Service added successfully!", Toast.LENGTH_SHORT).show()

                                    serviceName = ""
                                    serviceDescription = ""
                                    servicePrice = ""
                                    isServiceAvailable = true
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid numeric price.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill all fields and select a user.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Service")
                }

                if (availableServices.isNotEmpty()) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableServices) { service ->
                            ServiceItem(service)
                        }
                    }
                } else {
                    Text("No services available at the moment.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun ServiceItem(service: InHouseService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(service.name, style = MaterialTheme.typography.titleMedium)
            Text(service.description, style = MaterialTheme.typography.bodyMedium)
            Text("Price: \$${service.price}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (service.isAvailable) "Available" else "Not Available",
                style = MaterialTheme.typography.bodyMedium,
                color = if (service.isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInHouseServiceScreen() {
    val context = LocalContext.current
    val mockDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    LaunchedEffect(Unit) {
        mockDatabase.inHouseServiceDao().insertAll()
    }
    InHouseServiceScreen(database = mockDatabase)
}
