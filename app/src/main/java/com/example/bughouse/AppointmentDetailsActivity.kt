package com.example.bughouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.models.Appointment
import com.example.bughouse.models.AppointmentStatus
import com.example.bughouse.ui.theme.BugHouseTheme

class AppointmentDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appointmentId = intent.getStringExtra("APPOINTMENT_ID") ?: return
        enableEdgeToEdge()
        
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppointmentDetailsScreen(appointmentId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(appointmentId: String) {
    val context = LocalContext.current as ComponentActivity
    
    // Mock appointment data - in a real app, you would fetch this from a database
    val appointment = remember {
        Appointment(
            id = appointmentId,
            studentName = "John Doe",
            courseCode = "CSE1301",
            date = "March 15, 2024",
            time = "10:00 AM",
            duration = "1 hour",
            status = AppointmentStatus.SCHEDULED
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appointment Details",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DetailItem(
                        label = "Course Code",
                        value = appointment.courseCode,
                        icon = Icons.Default.School
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailItem(
                        label = "Student Name",
                        value = appointment.studentName,
                        icon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailItem(
                        label = "Date",
                        value = appointment.date,
                        icon = Icons.Default.DateRange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailItem(
                        label = "Time",
                        value = appointment.time,
                        icon = Icons.Default.Schedule
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailItem(
                        label = "Duration",
                        value = appointment.duration,
                        icon = Icons.Default.Timer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailItem(
                        label = "Status",
                        value = appointment.status.name,
                        icon = Icons.Default.Info
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF0795DD)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 