package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch

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
    val scrollState = rememberScrollState()

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
                    // Will navigate to tutor profile screen
                    val intent = Intent(context, UpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onUpcomingAppointmentsClick = {
                    // Will navigate to upcoming appointments screen
                    val intent = Intent(context, YourAppointmentsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onPastAppointmentsClick = {
                    // Will navigate to past appointments screen
                    // For now, reusing YourAppointmentsActivity with a parameter
                    val intent = Intent(context, YourAppointmentsActivity::class.java)
                    intent.putExtra("showPastAppointments", true)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    // Will navigate to contact us screen
                    val intent = Intent(context, ContactUsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    // Will navigate to about screen
                    val intent = Intent(context, AboutActivity::class.java)
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
                currentScreen = "Tutor Dashboard" // Mark this as the Home screen
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Tutor Dashboard",
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
                    ),
                    actions = {
                        // This will be for the profile icon that expands to show settings
                        IconButton(onClick = {
                            val intent = Intent(context, UpdateProfileActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile and Settings",
                                tint = Color.White
                            )
                        }
                    }
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
                TutorDashboardContent()
            }
        }
    }
}

@Composable
fun TutorDashboardContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar Card
        TutorDashboardCard(
            title = "Calendar",
            icon = Icons.Default.CalendarMonth,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Navigate to the calendar screen
                val intent = Intent(context, TutorCalendarActivity::class.java)
                context.startActivity(intent)
            }
        )

        // Upcoming Appointments Section
        Text(
            text = "Upcoming Appointments",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF0795DD),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        
        // Placeholder for appointments list - in real app, this would be a LazyColumn with real data
        repeat(3) { index ->
            AppointmentItem(
                studentName = "Student ${index + 1}",
                courseCode = "CSE ${1300 + index}",
                dateTime = "May ${10 + index}, 2025 | 10:${30 + index} AM",
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Attendance Logs Search Card
        TutorDashboardCard(
            title = "Search Attendance Logs",
            icon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Navigate to attendance logs search screen
                val intent = Intent(context, TutorAttendanceLogsActivity::class.java)
                context.startActivity(intent)
            }
        )
        
        // Past Appointments Card
        TutorDashboardCard(
            title = "View Past Sessions",
            icon = Icons.Default.History,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Navigate to past appointments screen
                val intent = Intent(context, YourAppointmentsActivity::class.java)
                intent.putExtra("showPastAppointments", true)
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun AppointmentItem(
    studentName: String,
    courseCode: String,
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFE6F3FA),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = studentName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            
            Text(
                text = courseCode,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
            
            Text(
                text = dateTime,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun TutorDashboardCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon with circle background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE6F3FA), shape = RoundedCornerShape(percent = 50))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Title
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
        }
    }
} 