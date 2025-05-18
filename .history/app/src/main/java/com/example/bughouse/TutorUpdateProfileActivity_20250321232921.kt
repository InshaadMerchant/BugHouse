package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import com.example.bughouse.TutorDashboardActivity
import com.example.bughouse.TutorUpdateProfileActivity
import com.example.bughouse.ManageAppointmentsActivity
import com.example.bughouse.TutorAttendanceLogsActivity
import com.example.bughouse.TutorContactUsActivity
import com.example.bughouse.MainActivity

class TutorUpdateProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorUpdateProfileScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorUpdateProfileScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Form fields
    var fullName by remember { mutableStateOf("John Doe") }
    var employeeId by remember { mutableStateOf("1234567890") }
    var email by remember { mutableStateOf("john@mavs.uta.edu") }
    var department by remember { mutableStateOf("Computer Science") }
    var expertise by remember { mutableStateOf("Cloud Computing, System Design") }
    var officeHours by remember { mutableStateOf("Mon, Wed 10:00 AM - 2:00 PM") }

    // Error states
    var fullNameError by remember { mutableStateOf(false) }
    var employeeIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var departmentError by remember { mutableStateOf(false) }
    var expertiseError by remember { mutableStateOf(false) }
    var officeHoursError by remember { mutableStateOf(false) }

    // Edit mode state
    var isEditMode by remember { mutableStateOf(false) }

    // Validation functions
    val validateFullName = { name: String ->
        name.isNotBlank() && name.length >= 2
    }

    val validateEmployeeId = { id: String ->
        id.length == 10 && TextUtils.isDigitsOnly(id)
    }

    val validateEmail = { email: String ->
        email.isNotBlank() && email.contains("@") && email.endsWith(".edu")
    }

    val validateDepartment = { dept: String ->
        dept.isNotBlank()
    }

    val validateExpertise = { exp: String ->
        exp.isNotBlank()
    }

    val validateOfficeHours = { hours: String ->
        hours.isNotBlank()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TutorNavigationDrawerContent(
                onHomeClick = {
                    val intent = Intent(context, TutorDashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    scope.launch { drawerState.close() }
                },
                onAppointmentsClick = {
                    val intent = Intent(context, ManageAppointmentsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    val intent = Intent(context, TutorContactUsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    val intent = Intent(context, TutorAboutActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
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
                            text = "Tutor Profile",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFF0795DD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TD",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Full Name Field
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    enabled = isEditMode,
                    isError = fullNameError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                // Employee ID Field
                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it },
                    label = { Text("Employee ID") },
                    enabled = isEditMode,
                    isError = employeeIdError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    enabled = isEditMode,
                    isError = emailError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                // Department Field
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    enabled = isEditMode,
                    isError = departmentError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                // Expertise Field
                OutlinedTextField(
                    value = expertise,
                    onValueChange = { expertise = it },
                    label = { Text("Areas of Expertise") },
                    enabled = isEditMode,
                    isError = expertiseError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                // Office Hours Field
                OutlinedTextField(
                    value = officeHours,
                    onValueChange = { officeHours = it },
                    label = { Text("Office Hours") },
                    enabled = isEditMode,
                    isError = officeHoursError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0795DD),
                        focusedLabelColor = Color(0xFF0795DD)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Save Button
                if (isEditMode) {
                    Button(
                        onClick = {
                            // Validate all fields
                            fullNameError = !validateFullName(fullName)
                            employeeIdError = !validateEmployeeId(employeeId)
                            emailError = !validateEmail(email)
                            departmentError = !validateDepartment(department)
                            expertiseError = !validateExpertise(expertise)
                            officeHoursError = !validateOfficeHours(officeHours)

                            if (!fullNameError && !employeeIdError && !emailError && 
                                !departmentError && !expertiseError && !officeHoursError) {
                                // Save changes
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                isEditMode = false
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

                // Back to Home Button
                Button(
                    onClick = {
                        val intent = Intent(context, TutorDashboardActivity::class.java)
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
                        "Back to Home",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
} 