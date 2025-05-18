package com.example.bughouse

import ApiClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.DTO.CreateAppointmentRequest
import com.example.bughouse.ui.theme.BugHouseTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ConfirmBookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get booking details from intent
        val courseId = intent.getStringExtra("COURSE_ID") ?: "Unknown Course"
        val courseName = intent.getStringExtra("COURSE_NAME") ?: "Unknown Course Name"
        val actualCourseId = intent.getIntExtra("ACTUAL_COURSE_ID", 1)
        val selectedDateStr = intent.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()
        val tutorId = intent.getIntExtra("TUTOR_ID", 1)
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown Tutor"
        val tutorRating = intent.getDoubleExtra("TUTOR_RATING", 0.0)
        val timeSlot = intent.getStringExtra("TIME_SLOT") ?: "Unknown Time"

        // Log received data for debugging
        Log.d("ConfirmBookingActivity", "Course: $courseId ($actualCourseId), Date: $selectedDateStr, Tutor: $tutorName ($tutorId), Time: $timeSlot")

        // Parse date
        val selectedDate = LocalDate.parse(selectedDateStr)

        // Get current user ID and student ID from shared preferences or app state
        // For this example, we're using the Milan Singh user ID from your database
        val currentUserId = "7396c45b-9c89-4004-8d90-709473acc920" // Milan Singh's user ID
        val currentStudentId = 1 // Milan Singh's student ID

        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConfirmBookingScreen(
                        courseId = courseId,
                        courseName = courseName,
                        actualCourseId = actualCourseId,
                        selectedDate = selectedDate,
                        tutorId = tutorId,
                        tutorName = tutorName,
                        tutorRating = tutorRating,
                        timeSlot = timeSlot,
                        studentId = currentStudentId,
                        userId = currentUserId,
                        onBackPressed = { finish() },
                        onConfirmBooking = { success, message ->
                            if (success) {
                                // Generate a unique booking ID (this is just for display, the real ID is in the database)
                                val bookingId = "BK${System.currentTimeMillis() % 10000}"

                                // Navigate to booking confirmation screen
                                val intent = Intent(this, BookingConfirmationActivity::class.java)
                                intent.putExtra("COURSE_ID", courseId)
                                intent.putExtra("COURSE_NAME", courseName)
                                intent.putExtra("SELECTED_DATE", selectedDateStr)
                                intent.putExtra("TUTOR_NAME", tutorName)
                                intent.putExtra("TIME_SLOT", timeSlot)
                                intent.putExtra("BOOKING_ID", bookingId)
                                startActivity(intent)

                                // Close this activity
                                finish()
                            } else {
                                // Show error message
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookingScreen(
    courseId: String,
    courseName: String,
    actualCourseId: Int,
    selectedDate: LocalDate,
    tutorId: Int,
    tutorName: String,
    tutorRating: Double,
    timeSlot: String,
    studentId: Int,
    userId: String,
    onBackPressed: () -> Unit,
    onConfirmBooking: (success: Boolean, message: String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Confirm Booking",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Booking summary box
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Booking Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F7FFF)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    BookingDetailItem(
                        icon = Icons.Default.School,
                        label = "Course",
                        value = "$courseId - $courseName"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookingDetailItem(
                        icon = Icons.Default.Person,
                        label = "Tutor",
                        value = tutorName
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookingDetailItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Date",
                        value = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookingDetailItem(
                        icon = Icons.Default.Schedule,
                        label = "Time",
                        value = formatTimeForDisplay(timeSlot)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm button
            Button(
                onClick = {
                    isLoading = true

                    // Create appointment request
                    val request = CreateAppointmentRequest(
                        studentId = studentId,
                        tutorId = tutorId,
                        courseId = actualCourseId,
                        appointmentDate = selectedDate.toString(), // Format: YYYY-MM-DD
                        startTime = timeSlot, // Format from database: HH:MM:SS
                        duration = 60, // Default to 60 minutes
                        status = "scheduled"
                    )

                    Log.d("ConfirmBookingActivity", "Creating appointment: $request")

                    // Call API to create appointment
                    ApiClient.apiService.createAppointment(request).enqueue(object : Callback<Map<String, String>> {
                        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                            isLoading = false
                            if (response.isSuccessful) {
                                Log.d("ConfirmBookingActivity", "Appointment created successfully")
                                onConfirmBooking(true, "Appointment scheduled successfully!")
                            } else {
                                Log.e("ConfirmBookingActivity", "Failed to create appointment: ${response.code()} - ${response.message()}")
                                onConfirmBooking(false, "Failed to schedule appointment. Please try again.")
                            }
                        }

                        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                            isLoading = false
                            Log.e("ConfirmBookingActivity", "Network error creating appointment", t)
                            onConfirmBooking(false, "Network error. Please check your connection and try again.")
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F7FFF)
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
                        text = "Confirm Booking",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel button
            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3F7FFF))
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F7FFF)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BookingDetailItem(
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

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
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

// Helper function to format time for display
fun formatTimeForDisplay(timeString: String): String {
    // Check if timeString already has a nice format
    if (timeString.contains("AM") || timeString.contains("PM")) {
        return timeString
    }

    try {
        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        return "$hour12:${minute.toString().padStart(2, '0')} $amPm"
    } catch (e: Exception) {
        Log.e("ConfirmBookingActivity", "Error formatting time: $timeString", e)
        return timeString // Return original if parsing fails
    }
}