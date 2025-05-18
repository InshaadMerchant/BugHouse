package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
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
import com.example.bughouse.APIService.ApiInterface
import com.example.bughouse.ui.theme.BugHouseTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutorCheckinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get appointment details from intent
        val studentName = intent.getStringExtra("studentName") ?: "Unknown Student"
        val courseCode = intent.getStringExtra("courseCode") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"
        val appointmentId = intent.getIntExtra("appointmentId", -1)

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorCheckinScreen(
                        studentName = studentName,
                        courseCode = courseCode,
                        appointmentTime = appointmentTime,
                        appointmentDate = appointmentDate,
                        appointmentId = appointmentId,
                        onBackPressed = {
                            // Just finish this activity to return to the dashboard
                            finish()
                        },
                        onCheckinClick = {
                            // Call the API to update the appointment status to checked in
                            ApiClient.apiService.checkinAppointment(appointmentId).enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                    if (response.isSuccessful) {
                                        // Navigate to TutorCheckinSummaryActivity
                                        val intent = Intent(this@TutorCheckinActivity, TutorCheckinSummaryActivity::class.java).apply {
                                            putExtra("studentName", studentName)
                                            putExtra("courseCode", courseCode)
                                            putExtra("appointmentTime", appointmentTime)
                                            putExtra("appointmentDate", appointmentDate)
                                            putExtra("appointmentId", appointmentId)
                                        }
                                        startActivity(intent)
                                        finish()
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    // Error handling would be here
                                }
                            })
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorCheckinScreen(
    studentName: String,
    courseCode: String,
    appointmentTime: String,
    appointmentDate: String,
    appointmentId: Int,
    onBackPressed: () -> Unit,
    onCheckinClick: () -> Unit
) {
    var isCheckedIn by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Check-in",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF0795DD),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = studentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFF0795DD),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = courseCode,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "$appointmentDate, $appointmentTime",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-in button
            Button(
                onClick = {
                    isCheckedIn = true
                    onCheckinClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isCheckedIn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCheckedIn) Color.Gray else Color(0xFF0795DD)
                )
            ) {
                if (isCheckedIn) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Checked In")
                } else {
                    Text("Check In Student")
                }
            }
        }
    }
}