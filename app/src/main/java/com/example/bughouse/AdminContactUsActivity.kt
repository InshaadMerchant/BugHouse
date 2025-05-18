package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch

class AdminContactUsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminContactUsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContactUsScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Form fields state
    var employeeId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Validation states
    var employeeIdError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var subjectError by remember { mutableStateOf(false) }
    var messageError by remember { mutableStateOf(false) }

    // Validation functions
    val validateEmployeeId = { id: String ->
        id.length == 10 && id.isDigitsOnly()
    }

    val validateName = { input: String ->
        input.isNotBlank()
    }

    val validateSubject = { input: String ->
        input.isNotBlank()
    }

    val validateMessage = { input: String ->
        input.length >= 10
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminNavigationDrawerContent(
                onHomeClick = {
                    // Navigate to AdminDashboardActivity
                    val intent = Intent(context, AdminDashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    // Navigate to AdminUpdateProfileActivity
                    val intent = Intent(context, AdminUpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    // Already on this screen
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    val intent = Intent(context, AdminAboutActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    // Navigate back to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "Contact Us" // Mark this as the current screen
            )
        },
        gesturesEnabled = true
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            when {
                                dragAmount > 50 -> scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                }
                                dragAmount < -50 -> scope.launch {
                                    if (drawerState.isOpen) drawerState.close()
                                }
                            }
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Send Us a Message",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0795DD),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Contact Form Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Employee ID Field
                            OutlinedTextField(
                                value = employeeId,
                                onValueChange = {
                                    if (it.length <= 10 && it.isDigitsOnly()) {
                                        employeeId = it
                                        employeeIdError = it.isNotEmpty() && !validateEmployeeId(it)
                                    }
                                },
                                label = { Text("Employee ID (10 digits)") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = employeeIdError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )

                            if (employeeIdError) {
                                Text(
                                    text = "Please enter a valid 10-digit Employee ID",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name Field
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = it.isNotEmpty() && !validateName(it)
                                },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = nameError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                )
                            )

                            if (nameError) {
                                Text(
                                    text = "Please enter your full name",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Subject Field
                            OutlinedTextField(
                                value = subject,
                                onValueChange = {
                                    subject = it
                                    subjectError = it.isNotEmpty() && !validateSubject(it)
                                },
                                label = { Text("Subject") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = subjectError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                )
                            )

                            if (subjectError) {
                                Text(
                                    text = "Please enter a subject",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Message Field
                            OutlinedTextField(
                                value = message,
                                onValueChange = {
                                    message = it
                                    messageError = it.isNotEmpty() && !validateMessage(it)
                                },
                                label = { Text("Message") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                isError = messageError,
                                singleLine = false,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                )
                            )

                            if (messageError) {
                                Text(
                                    text = "Message should be at least 10 characters long",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Send Button
                            Button(
                                onClick = {
                                    // Validate all fields
                                    employeeIdError = !validateEmployeeId(employeeId)
                                    nameError = !validateName(name)
                                    subjectError = !validateSubject(subject)
                                    messageError = !validateMessage(message)

                                    if (!employeeIdError && !nameError && !subjectError && !messageError) {
                                        // Send message
                                        Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()

                                        // Clear fields after sending
                                        employeeId = ""
                                        name = ""
                                        subject = ""
                                        message = ""
                                    } else {
                                        Toast.makeText(context, "Please fix the errors in the form", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                            ) {
                                Text(
                                    "Send Message",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Emergency contact info
                    Text(
                        text = "For urgent administrative matters requiring immediate assistance, please contact: admin-support@bughouse.com",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}