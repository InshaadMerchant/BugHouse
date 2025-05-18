package com.example.bughouse

import ApiClient
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get appointment details from intent
        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val tutorName = intent.getStringExtra("tutorName") ?: "Unknown Tutor"
        val courseName = intent.getStringExtra("courseName") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckinScreen(
                        appointmentId = appointmentId,
                        tutorName = tutorName,
                        courseName = courseName,
                        appointmentTime = appointmentTime,
                        appointmentDate = appointmentDate,
                        onBackPressed = { finish() },
                        onCheckinSuccess = {
                            // Navigate to CheckinSummaryActivity
                            val intent = Intent(this, CheckinSummaryActivity::class.java).apply {
                                putExtra("tutorName", tutorName)
                                putExtra("courseName", courseName)
                                putExtra("appointmentTime", appointmentTime)
                                putExtra("appointmentDate", appointmentDate)
                            }
                            startActivity(intent)
                            finish() // Close this activity
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinScreen(
    appointmentId: Int,
    tutorName: String,
    courseName: String,
    appointmentTime: String,
    appointmentDate: String,
    onBackPressed: () -> Unit,
    onCheckinSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Function to check in to the appointment
    fun checkinToAppointment() {
        if (appointmentId == -1) {
            error = "Invalid appointment ID"
            return
        }

        isLoading = true
        error = null

        ApiClient.apiService.checkinAppointment(appointmentId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully checked in!", Toast.LENGTH_SHORT).show()
                    onCheckinSuccess()
                } else {
                    error = "Check-in failed: ${response.message()}"
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Check In",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instruction text
                Text(
                    text = "Check In to Your Appointment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = "Please check in to confirm your attendance for this tutoring session. " +
                            "Checking in will notify your tutor that you have arrived.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Error message if any
                if (error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = error ?: "",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }

                // Appointment details card
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
                        Text(
                            text = "Appointment Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD)
                        )

                        // Tutor name
                        AppointmentDetailRow(
                            icon = Icons.Default.Person,
                            label = "Tutor:",
                            value = tutorName
                        )

                        // Course name
                        AppointmentDetailRow(
                            icon = Icons.Default.School,
                            label = "Course:",
                            value = courseName
                        )

                        // Date
                        AppointmentDetailRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Date:",
                            value = appointmentDate
                        )

                        // Time
                        AppointmentDetailRow(
                            icon = Icons.Default.Timer,
                            label = "Time:",
                            value = appointmentTime
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Important notices
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Important Notes:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD),
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• You can only check in within 15 minutes before your appointment",
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "• If you're running late, please contact your tutor directly",
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "• Sessions typically last for 60 minutes",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Check in button
                Button(
                    onClick = { checkinToAppointment() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0795DD),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Check In Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AppointmentDetailRow(
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
            modifier = Modifier.width(60.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}