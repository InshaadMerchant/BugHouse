package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

class ReviewSubmissionConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get review details from intent
        val tutorName = intent.getStringExtra("tutorName") ?: "Unknown Tutor"
        val courseName = intent.getStringExtra("courseName") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"
        val rating = intent.getIntExtra("rating", 0)

        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReviewSubmissionConfirmationScreen(
                        tutorName = tutorName,
                        courseName = courseName,
                        appointmentDate = appointmentDate,
                        appointmentTime = appointmentTime,
                        rating = rating,
                        onBackToDashboard = {
                            // Navigate back to dashboard with clear flags
                            val intent = Intent(this, TutorDashboardActivity::class.java)
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
fun ReviewSubmissionConfirmationScreen(
    tutorName: String,
    courseName: String,
    appointmentDate: String,
    appointmentTime: String,
    rating: Int,
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
                text = "Review Submitted!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0795DD)  // Using your app's blue color
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submission ID
            val submissionId = "RV${System.currentTimeMillis() % 10000}"
            Text(
                text = "Submission ID: $submissionId",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Review summary card
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
                        text = "Review Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReviewConfirmationDetailItem(
                        icon = Icons.Default.Person,
                        detail = "Tutor: $tutorName"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ReviewConfirmationDetailItem(
                        icon = Icons.Default.School,
                        detail = "Course: $courseName"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ReviewConfirmationDetailItem(
                        icon = Icons.Default.CalendarToday,
                        detail = "Date: $appointmentDate"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ReviewConfirmationDetailItem(
                        icon = Icons.Default.Schedule,
                        detail = "Time: $appointmentTime"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating stars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF0795DD),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Rating: ",
                            fontSize = 16.sp
                        )

                        Row {
                            repeat(rating) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmation message
            Text(
                text = "Thank you for submitting your review. Your feedback helps us improve our tutoring services and helps other students find the right tutors.",
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
                containerColor = Color(0xFF0795DD)  // Using your app's blue color
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
fun ReviewConfirmationDetailItem(
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
            tint = Color(0xFF0795DD),  // Using your app's blue color
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = detail,
            fontSize = 16.sp
        )
    }
}