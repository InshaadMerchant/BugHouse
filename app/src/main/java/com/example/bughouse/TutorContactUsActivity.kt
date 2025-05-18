package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import com.example.bughouse.components.TutorNavigationDrawer
import kotlinx.coroutines.launch

class TutorContactUsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorContactUsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorContactUsScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var employeeId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Validation states
    var employeeIdError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var subjectError by remember { mutableStateOf(false) }
    var messageError by remember { mutableStateOf(false) }

    TutorNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        currentScreen = "Contact Us"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Contact Us",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0795DD),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Employee ID Field
                OutlinedTextField(
                    value = employeeId,
                    onValueChange = {
                        employeeId = it
                        employeeIdError = it.isEmpty()
                    },
                    label = { Text("Employee ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = employeeIdError,
                    supportingText = if (employeeIdError) {
                        { Text("Employee ID is required") }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Employee ID") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD),
                        cursorColor = Color(0xFF0795DD)
                    )
                )

                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isEmpty()
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name is required") }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD),
                        cursorColor = Color(0xFF0795DD)
                    )
                )

                // Subject Field
                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        subjectError = it.isEmpty()
                    },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = subjectError,
                    supportingText = if (subjectError) {
                        { Text("Subject is required") }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Subject, contentDescription = "Subject") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD),
                        cursorColor = Color(0xFF0795DD)
                    )
                )

                // Message Field
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        messageError = it.isEmpty()
                    },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    isError = messageError,
                    supportingText = if (messageError) {
                        { Text("Message is required") }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Message, contentDescription = "Message") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD),
                        cursorColor = Color(0xFF0795DD)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Submit Button
                Button(
                    onClick = {
                        employeeIdError = employeeId.isEmpty()
                        nameError = name.isEmpty()
                        subjectError = subject.isEmpty()
                        messageError = message.isEmpty()

                        if (!employeeIdError && !nameError && !subjectError && !messageError) {
                            // Handle form submission
                            Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show()
                            // Clear form
                            employeeId = ""
                            name = ""
                            subject = ""
                            message = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Message")
                }
            }
        }
    }
}

@Composable
fun FAQItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF0795DD)
            )
        ) {
            Text(
                text = question,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Show less" else "Show more"
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
} 