package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun TeacherImageScreen(navController: NavHostController) {
    // For demonstration, we use a placeholder state for the image.
    var teacherImage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Image") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("studentImage") }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Upload Teacher Answer Key")
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    if (teacherImage == null) {
                        Text("Teacher Image Preview")
                    } else {
                        Text("Processed Teacher Image")
                    }
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Launch camera intent */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Photo")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Launch gallery intent */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { /* Rotate image */ }) { Text("Rotate") }
                    Button(onClick = { /* Flip image */ }) { Text("Flip") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Confirm: Save teacher image to cache and navigate.
                        // ImageCache.getInstance().addTeacherImage(teacherBitmap)
                        navController.navigate("studentImage")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm")
                }
            }
        }
    )
}
