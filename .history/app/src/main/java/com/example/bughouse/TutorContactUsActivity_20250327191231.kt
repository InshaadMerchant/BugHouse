package com.example.bughouse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import com.example.bughouse.TutorAboutActivity

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
                    val intent = Intent(context, TutorUpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAppointmentsClick = {
                    val intent = Intent(context, ManageAppointmentsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAttendanceLogsClick = {
                    val intent = Intent(context, TutorAttendanceLogsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
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
                currentScreen = "Contact Us"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Contact Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Contact Us",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Need help with your tutoring sessions or have questions? Our support team is here to assist you.",
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Form Fields
                        OutlinedTextField(
                            value = employeeId,
                            onValueChange = {
                                employeeId = it
                                employeeIdError = it.isNotEmpty() && !validateEmployeeId(it)
                            },
                            label = { Text("Employee ID") },
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
                                text = "Please enter a valid 10-digit employee ID",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = it.isNotEmpty() && !validateName(it)
                            },
                            label = { Text("Name") },
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
                                text = "Please enter your name",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }

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