package com.sn0w_w0lf.luxevista

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPromotionActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                AddPromotionScreen(database)
            }
        }
    }

    fun addPromotion(promotion: Promotion) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.promotionDao().insertPromotion(promotion)
            launch(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Promotion added successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            }
        }
    }
}

@Composable
fun AddPromotionScreen(database: AppDatabase) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var discountPercentage by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.Center),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Promotion", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Promotion Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Promotion Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = discountPercentage,
                    onValueChange = { discountPercentage = it },
                    label = { Text("Discount Percentage") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (title.text.isNotEmpty() && description.text.isNotEmpty() && discountPercentage.text.isNotEmpty()) {
                            val promotion = Promotion(
                                title = title.text,
                                description = description.text,
                                discountPercentage = discountPercentage.text.toInt()
                            )
                            (context as? AddPromotionActivity)?.addPromotion(promotion)
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Promotion")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddPromotionScreen() {
    MaterialTheme {
        AddPromotionScreen(database = AppDatabase.getDatabase(LocalContext.current))
    }
}
