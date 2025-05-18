package com.example.bughouse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
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

class AdminUpdateProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminUpdateProfileScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUpdateProfileScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // States for form fields with redundant data for testing
    var fullName by remember { mutableStateOf("Admin User") }
    var employeeId by remember { mutableStateOf("1234567890") } // 10-digit ID
    var email by remember { mutableStateOf("admin@bughouse.edu") }

    // States for validation
    var fullNameError by remember { mutableStateOf(false) }
    var employeeIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    // State for profile image selection (simplified without using actual image)
    var isImageSelected by remember { mutableStateOf(true) } // Assuming user already has a profile image

    // State for edit mode
    var isEditMode by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Just mark that an image was selected, without trying to display it
        if (uri != null) {
            isImageSelected = true
        }
    }

    // Validation functions
    val validateFullName = { name: String ->
        name.isNotBlank()
    }

    val validateEmployeeId = { id: String ->
        id.length == 10 && id.isDigitsOnly()
    }

    val validateEmail = { email: String ->
        email.isNotBlank() && email.contains("@") && (email.endsWith(".edu") || email.endsWith(".com"))
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
                    // Stay on current screen
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    val intent = Intent(context, AdminContactUsActivity::class.java)
                    context.startActivity(intent)
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
                currentScreen = "Profile"
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Admin Profile",
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
                    actions = {
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White
                            )
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
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status indicator for edit mode
                    AnimatedVisibility(visible = isEditMode) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                        ) {
                            Text(
                                text = "You're in edit mode. Make your changes and tap Save Changes.",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF7E6514)
                            )
                        }
                    }

                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(if (isImageSelected) Color(0xFF0795DD) else Color(0xFFE6F3FA))
                            .clickable(enabled = isEditMode) {
                                if (isEditMode) {
                                    imagePickerLauncher.launch("image/*")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isImageSelected) {
                            // Show placeholder with "A" for Admin
                            Text(
                                text = "A",
                                color = Color.White,
                                fontSize = 60.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            // Show placeholder
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Upload Image",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF0795DD)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Upload Photo",
                                    fontSize = 14.sp,
                                    color = Color(0xFF0795DD),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Form Fields in a Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Full Name Field
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    if (isEditMode) {
                                        fullName = it
                                        fullNameError = !validateFullName(it)
                                    }
                                },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = fullNameError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                enabled = isEditMode
                            )

                            if (fullNameError) {
                                Text(
                                    text = "Please enter your full name",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Employee ID Field
                            OutlinedTextField(
                                value = employeeId,
                                onValueChange = {
                                    if (isEditMode && it.length <= 10 && it.isDigitsOnly()) {
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
                                ),
                                enabled = isEditMode
                            )

                            if (employeeIdError) {
                                Text(
                                    text = "Employee ID must be exactly 10 digits",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Email Field
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    if (isEditMode) {
                                        email = it
                                        emailError = it.isNotEmpty() && !validateEmail(it)
                                    }
                                },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = emailError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                enabled = isEditMode
                            )

                            if (emailError) {
                                Text(
                                    text = "Please enter a valid email address",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Save Changes Button (only visible in edit mode)
                    AnimatedVisibility(visible = isEditMode) {
                        Button(
                            onClick = {
                                // Validate all fields
                                fullNameError = !validateFullName(fullName)
                                employeeIdError = !validateEmployeeId(employeeId)
                                emailError = !validateEmail(email)

                                if (!fullNameError && !employeeIdError && !emailError) {
                                    // Validation successful - save changes
                                    Toast.makeText(context, "Admin profile updated successfully", Toast.LENGTH_SHORT).show()
                                    isEditMode = false // Exit edit mode after successful save
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
                                "Save Changes",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Dashboard Button (always visible)
                    Button(
                        onClick = {
                            // Navigate back to Admin Dashboard
                            val intent = Intent(context, AdminDashboardActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) Color.Gray else Color(0xFF0795DD)
                        )
                    ) {
                        Text(
                            "Back to Dashboard",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}