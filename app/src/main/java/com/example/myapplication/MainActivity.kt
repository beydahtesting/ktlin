package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface {
                    MyApplicationApp()
                }
            }
        }
    }
}

@Composable
fun MyApplicationApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("teacherImage") { TeacherImageScreen(navController) }
        composable("studentImage") { StudentImageScreen(navController) }
        composable("result") { ResultScreen(navController) }
        composable("studentRecords") { StudentRecordsScreen(navController) }
    }
}
