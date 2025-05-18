package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BookingConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get booking details from intent
        val courseId = intent.getStringExtra("COURSE_ID") ?: "Unknown Course"
        val courseName = intent.getStringExtra("COURSE_NAME") ?: "Unknown Course Name"
        val selectedDateStr = intent.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown Tutor"
        val timeSlot = intent.getStringExtra("TIME_SLOT") ?: "Unknown Time"
        val bookingId = intent.getStringExtra("BOOKING_ID") ?: "BK${System.currentTimeMillis() % 10000}"

        // Parse date
        val selectedDate = LocalDate.parse(selectedDateStr)

        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookingConfirmationScreen(
                        bookingId = bookingId,
                        courseId = courseId,
                        courseName = courseName,
                        selectedDate = selectedDate,
                        tutorName = tutorName,
                        timeSlot = timeSlot,
                        onBackToDashboard = {
                            // Navigate back to dashboard with clear flags
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

@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    courseId: String,
    courseName: String,
    selectedDate: LocalDate,
    tutorName: String,
    timeSlot: String,
    onBackToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section (empty space)
        Spacer(modifier = Modifier.height(32.dp))

        // Middle section with confirmation and details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Success icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation text
            Text(
                text = "Booking Confirmed!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F7FFF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Booking ID
            Text(
                text = "Booking ID: $bookingId",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Booking summary card
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
                    Text(
                        text = "Booking Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookingConfirmationDetailItem(
                        icon = Icons.Default.School,
                        detail = "$courseId - $courseName"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookingConfirmationDetailItem(
                        icon = Icons.Default.Person,
                        detail = "Tutor: $tutorName"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookingConfirmationDetailItem(
                        icon = Icons.Default.CalendarToday,
                        detail = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookingConfirmationDetailItem(
                        icon = Icons.Default.Schedule,
                        detail = "Time: $timeSlot"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmation message
            Text(
                text = "Your tutoring session has been successfully booked. You will receive a notification before your session starts.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }

        // Bottom section with button
        Button(
            onClick = onBackToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F7FFF)
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

@Composable
fun BookingConfirmationDetailItem(
    icon: ImageVector,
    detail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3F7FFF),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = detail,
            fontSize = 16.sp
        )
    }
}