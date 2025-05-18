package com.example.bughouse

import android.content.Intent
import android.os.Bundle
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

class TutorAboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorAboutScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorAboutScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TutorNavigationDrawerContent(
                onHomeClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, TutorDashboardActivity::class.java.name)
                    }
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, TutorUpdateProfileActivity::class.java.name)
                    }
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onUpcomingAppointmentsClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, ManageAppointmentsActivity::class.java.name)
                    }
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onPastAppointmentsClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, TutorAttendanceLogsActivity::class.java.name)
                    }
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, TutorContactUsActivity::class.java.name)
                    }
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    val intent = Intent().apply {
                        setClassName(context.packageName, MainActivity::class.java.name)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "About"
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "About BugHouse",
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
                // App Description Card
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
                            text = "Welcome to BugHouse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "BugHouse is a comprehensive tutoring management system designed to streamline the tutoring experience at UTA. As a tutor, this platform helps you manage your sessions, track student progress, and provide effective academic support.",
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Key Features:",
                            fontWeight = FontWeight.Bold
                        )

                        FeatureItem(
                            icon = Icons.Default.Schedule,
                            title = "Session Management",
                            description = "Easily schedule and manage your tutoring sessions with students."
                        )

                        FeatureItem(
                            icon = Icons.Default.CheckCircle,
                            title = "Check-in System",
                            description = "Simple check-in process to record attendance and session details."
                        )

                        FeatureItem(
                            icon = Icons.Default.Assessment,
                            title = "Progress Tracking",
                            description = "Monitor student progress and maintain session records."
                        )

                        FeatureItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            description = "Stay updated with session reminders and important announcements."
                        )
                    }
                }

                // Version Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Version Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Current Version: 1.0.0",
                            color = Color.Gray
                        )

                        Text(
                            text = "Last Updated: March 2024",
                            color = Color.Gray
                        )

                        Text(
                            text = "© 2024 UTA BugHouse. All rights reserved.",
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Back to Home Button
                Button(
                    onClick = {
                        context.startActivity(Intent().apply {
                            setClassName(context.packageName, TutorDashboardActivity::class.java.name)
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
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

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF0795DD),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
} 