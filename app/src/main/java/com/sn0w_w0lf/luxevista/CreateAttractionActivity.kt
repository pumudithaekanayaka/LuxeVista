package com.sn0w_w0lf.luxevista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class CreateAttractionActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                CreateAttractionScreen(database)
            }
        }
    }
}

@Composable
fun CreateAttractionScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var offer by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image), // Replace with your actual drawable resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add New Attraction",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Attraction Name") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                )

                OutlinedTextField(
                    value = offer,
                    onValueChange = { offer = it },
                    label = { Text("Exclusive Offer") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && description.isNotBlank() && offer.isNotBlank()) {
                            scope.launch {
                                database.attractionDao().insertAttraction(
                                    Attraction(
                                        name = name,
                                        description = description,
                                        offer = offer
                                    )
                                )
                                Toast.makeText(context, "Attraction added successfully!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Attraction")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateAttractionScreen() {
    CreateAttractionScreen(database = AppDatabase.getDatabase(LocalContext.current))
}
