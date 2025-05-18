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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme

class AdminRemoveTutorConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Extract data from intent
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown Tutor"
        val reason = intent.getStringExtra("REASON") ?: "Unknown Reason"
        val reasonCategory = intent.getStringExtra("REASON_CATEGORY") ?: "Unknown"
        val removalId = intent.getStringExtra("REMOVAL_ID") ?: "Unknown ID"
        val removalDate = intent.getStringExtra("REMOVAL_DATE") ?: "Unknown Date"

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White  // Set background to white
                ) {
                    TutorRemovalConfirmationScreen(
                        tutorName = tutorName,
                        reason = reason,
                        reasonCategory = reasonCategory,
                        removalId = removalId,
                        removalDate = removalDate,
                        onBackToDashboard = {
                            // Navigate back to dashboard with clear flags
                            val intent = Intent(this, AdminDashboardActivity::class.java)
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
fun TutorRemovalConfirmationScreen(
    tutorName: String,
    reason: String,
    reasonCategory: String,
    removalId: String,
    removalDate: String,
    onBackToDashboard: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Success icon with green circle background
        Surface(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            color = Color(0xFF6AC259) // Green color for success
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation text
        Text(
            text = "Tutor Removed\nSuccessfully!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F8AE0), // Blue color for heading
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Removal ID
        Text(
            text = "Removal ID: $removalId",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Heading above card
        Text(
            text = "Removal Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3F8AE0), // Blue color for heading
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 4.dp),
            textAlign = TextAlign.Left
        )

        // Removal summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                RemovalDetailItem(
                    icon = Icons.Default.Person,
                    iconTint = Color(0xFF3F8AE0),
                    label = "Tutor",
                    detail = tutorName
                )

                Spacer(modifier = Modifier.height(16.dp))

                RemovalDetailItem(
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF3F8AE0),
                    label = "Reason for Removal",
                    detail = if (reasonCategory == "Other") reason else reasonCategory
                )

                Spacer(modifier = Modifier.height(16.dp))

                RemovalDetailItem(
                    icon = Icons.Default.CalendarToday,
                    iconTint = Color(0xFF3F8AE0),
                    label = "Removal Date",
                    detail = removalDate
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional information card with icon
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F3FA)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF3F8AE0),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "The tutor has been removed from the system and can no longer access any tutoring resources. All associated students have been notified.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Back to Dashboard Button
        Button(
            onClick = onBackToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F8AE0) // Matching blue color
            )
        ) {
            Text(
                text = "Back to Dashboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RemovalDetailItem(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    detail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = iconTint
            )

            Text(
                text = detail,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}