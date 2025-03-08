package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("MCQ Scanner") }) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        AppMode.setOnlineMode(true)
                        navController.navigate("teacherImage")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Online Mode")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        AppMode.setOnlineMode(false)
                        navController.navigate("teacherImage")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Offline Mode")
                }
            }
        }
    )
}
