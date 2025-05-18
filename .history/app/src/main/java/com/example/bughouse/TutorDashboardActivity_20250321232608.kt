package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.bughouse.models.Appointment
import com.example.bughouse.models.AppointmentStatus
import com.example.bughouse.TutorNavigationDrawerContent

class TutorDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorDashboardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboardScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Selected day to view appointments
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var showDayAppointmentsDialog by remember { mutableStateOf(false) }

    // Hardcoded appointment data with days matching the current month
    val appointments = remember {
        listOf(
            Appointment(
                id = "1",
                studentName = "Tutor Past 1",
                courseCode = "Cloud Computing",
                date = "March 10, 2025",
                time = "10:00 AM",
                status = AppointmentStatus.COMPLETED
            ),
            Appointment(
                id = "2",
                studentName = "Tutor Past 3",
                courseCode = "System Design",
                date = "March 15, 2025",
                time = "2:30 PM",
                status = AppointmentStatus.COMPLETED
            ),
            Appointment(
                id = "3",
                studentName = "Tutor Past 4",
                courseCode = "Data Science",
                date = "March 20, 2025",
                time = "11:15 AM",
                status = AppointmentStatus.COMPLETED
            ),
            Appointment(
                id = "4",
                studentName = "Tutor Past 2",
                courseCode = "Data Analyst",
                date = "March 6, 2025",
                time = "9:00 AM",
                status = AppointmentStatus.COMPLETED
            ),
            Appointment(
                id = "5",
                studentName = "Tutor Past 5",
                courseCode = "Product Management",
                date = "March 24, 2025",
                time = "3:45 PM",
                status = AppointmentStatus.COMPLETED
            )
        )
    }
    val appointments1 = remember {
        listOf(
            Appointment(
                id = "6",
                studentName = "Tutor 1",
                courseCode = "Cloud Computing",
                date = "March 10, 2025",
                time = "10:00 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "7",
                studentName = "Tutor 3",
                courseCode = "System Design",
                date = "March 15, 2025",
                time = "2:30 PM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "8",
                studentName = "Tutor 4",
                courseCode = "Data Science",
                date = "March 20, 2025",
                time = "11:15 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "9",
                studentName = "Tutor 2",
                courseCode = "Data Analyst",
                date = "March 6, 2025",
                time = "9:00 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "10",
                studentName = "Tutor 5",
                courseCode = "Product Management",
                date = "March 24, 2025",
                time = "3:45 PM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "11",
                studentName = "Tutor 6",
                courseCode = "Machine Learning",
                date = "March 18, 2025",
                time = "1:00 PM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "12",
                studentName = "Tutor 7",
                courseCode = "Web Development",
                date = "March 22, 2025",
                time = "11:30 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "13",
                studentName = "Tutor 7(i)",
                courseCode = "Web Development Frontend",
                date = "March 22, 2025",
                time = "11:30 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "14",
                studentName = "Tutor 8",
                courseCode = "UI/UX Design",
                date = "March 27, 2025",
                time = "3:00 PM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "15",
                studentName = "Tutor 9",
                courseCode = "Software Engineering",
                date = "March 30, 2025",
                time = "10:45 AM",
                status = AppointmentStatus.SCHEDULED
            ),
            Appointment(
                id = "16",
                studentName = "Tutor 10",
                courseCode = "Database Management",
                date = "March 13, 2025",
                time = "2:15 PM",
                status = AppointmentStatus.SCHEDULED
            )
        )
    }

    // Get all days that have appointments
    val daysWithAppointments = remember {
        appointments1.map { it.date.split(" ")[1].replace(",", "").toInt() }.distinct()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TutorNavigationDrawerContent(
                onHomeClick = {
                    // Navigate to TutorDashboardActivity (refresh current screen)
                    val intent = Intent(context, TutorDashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    // Navigate to TutorUpdateProfileActivity
                    val intent = Intent(context, TutorUpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAppointmentsClick = {
                    val intent = Intent(context, TutorAppointmentsActivity::class.java)
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
                    // Navigate back to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "Home" // Mark this as the Home screen
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "BugHouse Tutor",
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    containerColor = Color(0xFF0795DD),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
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
                DashboardContent(
                    appointments = appointments,
                    daysWithAppointments = daysWithAppointments,
                    onManageAppointmentsClick = {
                        // Navigate to TutorManageAppointmentsActivity
                        val intent = Intent(context, ManageAppointmentsActivity::class.java)
                        context.startActivity(intent)
                    },
                    onDayClick = { day ->
                        selectedDay = day
                        showDayAppointmentsDialog = true
                    }
                )
            }
        }
    }

    // Schedule Appointment Dialog
    if (showScheduleDialog) {
        AnimatedVisibility(
            visible = showScheduleDialog,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
        ) {
            Dialog(onDismissRequest = { showScheduleDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Schedule Options",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = {
                                val intent = Intent(context, TutorCourseSelectionActivity::class.java)
                                context.startActivity(intent)
                                showScheduleDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                        ) {
                            Text(
                                "Schedule an Appointment",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { showScheduleDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text(
                                "Cancel",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Day Appointments Dialog - Shows when a day with appointments is clicked
    if (showDayAppointmentsDialog && selectedDay != null) {
        val dayAppointments = appointments1.filter { it.date.split(" ")[1].replace(",", "").toInt() == selectedDay }

        Dialog(onDismissRequest = { showDayAppointmentsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Appointments on March ${selectedDay}, 2025",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { showDayAppointmentsDialog = false }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // List of appointments for this day
                    if (dayAppointments.isNotEmpty()) {
                        LazyColumn {
                            items(dayAppointments) { appointment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            // Navigate to TutorCheckinActivity when appointment is clicked
                                            val intent = Intent(context, TutorCheckinActivity::class.java).apply {
                                                putExtra("studentName", appointment.studentName)
                                                putExtra("courseCode", appointment.courseCode)
                                                putExtra("appointmentTime", appointment.time)
                                                putExtra("appointmentDate", appointment.date)
                                                putExtra("isTutorFlow", true)
                                            }
                                            context.startActivity(intent)
                                            showDayAppointmentsDialog = false // Close the dialog
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFE)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Time column with circle background
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE6F3FA)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = appointment.time,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0795DD),
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Appointment details
                                        Column {
                                            Text(
                                                text = appointment.studentName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = appointment.courseCode,
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No appointments for this day",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
} 