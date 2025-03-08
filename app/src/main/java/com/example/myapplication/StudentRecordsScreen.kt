package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun StudentRecordsScreen(navController: NavHostController) {
    var isEditing by remember { mutableStateOf(false) }
    val records = remember { StudentRecord.getRecords() }
    var showDefaultsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Records") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // You can add an export icon here if needed.
                    IconButton(onClick = { /* call export functionality */ }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Export")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Roll", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Score", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text("Action", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        }
                    }
                    itemsIndexed(records) { index, record ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if (isEditing) {
                                var name by remember { mutableStateOf(record.name) }
                                var roll by remember { mutableStateOf(record.rollNumber) }
                                var score by remember { mutableStateOf(record.score) }
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = roll,
                                    onValueChange = { roll = it },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = score,
                                    onValueChange = { score = it },
                                    modifier = Modifier.weight(1f)
                                )
                                Button(onClick = {
                                    StudentRecord.getRecords()[index] = StudentRecord(name, roll, score)
                                }, modifier = Modifier.weight(1f)) {
                                    Text("Delete")
                                }
                            } else {
                                Text(record.name, modifier = Modifier.weight(1f))
                                Text(record.rollNumber, modifier = Modifier.weight(1f))
                                Text(record.score, modifier = Modifier.weight(1f))
                                Button(onClick = {
                                    StudentRecord.getRecords().removeAt(index)
                                }, modifier = Modifier.weight(1f)) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { isEditing = !isEditing }) {
                        Text(if (isEditing) "Save" else "Edit")
                    }
                    Button(onClick = {
                        StudentRecord.addRecord(StudentRecord(DefaultValues.name, DefaultValues.roll, DefaultValues.score))
                    }) {
                        Text("Add Record")
                    }
                    Button(onClick = {
                        StudentRecord.clearRecords()
                    }) {
                        Text("Clear Data")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDefaultsDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set Default Values")
                }
            }
        }
    )

    if (showDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showDefaultsDialog = false },
            title = { Text("Set Default Values") },
            text = {
                Column {
                    var defaultName by remember { mutableStateOf(DefaultValues.name) }
                    var defaultRoll by remember { mutableStateOf(DefaultValues.roll) }
                    var defaultScore by remember { mutableStateOf(DefaultValues.score) }
                    OutlinedTextField(
                        value = defaultName,
                        onValueChange = { defaultName = it },
                        label = { Text("Default Name") }
                    )
                    OutlinedTextField(
                        value = defaultRoll,
                        onValueChange = { defaultRoll = it },
                        label = { Text("Default Roll") }
                    )
                    OutlinedTextField(
                        value = defaultScore,
                        onValueChange = { defaultScore = it },
                        label = { Text("Default Score") }
                    )
                    // Save updated values when confirmed.
                    LaunchedEffect(Unit) { }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Update the DefaultValues using the local state.
                    // (In a real app, store the new values.)
                    DefaultValues.name = DefaultValues.name // Replace with new values from state.
                    DefaultValues.roll = DefaultValues.roll
                    DefaultValues.score = DefaultValues.score
                    showDefaultsDialog = false
                    Toast.makeText(navController.context, "Default values updated", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDefaultsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
