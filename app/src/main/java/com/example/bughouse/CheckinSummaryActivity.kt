package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckinSummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get appointment details from intent
        val tutorName = intent.getStringExtra("tutorName") ?: "Unknown Tutor"
        val courseName = intent.getStringExtra("courseName") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"

        // Generate a check-in ID and timestamp
        val checkinId = "CHK${System.currentTimeMillis() % 10000}"
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckinSummaryScreen(
                        tutorName = tutorName,
                        courseName = courseName,
                        appointmentTime = appointmentTime,
                        appointmentDate = appointmentDate,
                        checkinId = checkinId,
                        checkinTime = currentTime,
                        onBackPressed = { finish() },
                        onConfirmCheckin = {
                            // Navigate back to Dashboard
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinSummaryScreen(
    tutorName: String,
    courseName: String,
    appointmentTime: String,
    appointmentDate: String,
    checkinId: String,
    checkinTime: String,
    onBackPressed: () -> Unit,
    onConfirmCheckin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Check-in Summary",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Icon
            Box(
                modifier = Modifier.padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(100.dp)
                )
            }

            // Title
            Text(
                text = "Ready for your session!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0795DD)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Your tutor has been notified of your arrival",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Check-in details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Check-in ID and time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Check-in ID: $checkinId",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Time: $checkinTime",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Session Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0795DD)
                    )

                    // Tutor name
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "Tutor:",
                        value = tutorName
                    )

                    // Course name
                    DetailRow(
                        icon = Icons.Default.School,
                        label = "Course:",
                        value = courseName
                    )

                    // Date
                    DetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Date:",
                        value = appointmentDate
                    )

                    // Time
                    DetailRow(
                        icon = Icons.Default.Timer,
                        label = "Time:",
                        value = appointmentTime
                    )

                    // Location (assumed)
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location:",
                        value = "Online Session"
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Status message
                    Text(
                        text = "Status: Checked In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm button
            Button(
                onClick = onConfirmCheckin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0795DD),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back to Dashboard",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0795DD),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.width(70.dp)
        )

        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}