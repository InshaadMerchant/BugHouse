package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

class StudentReportConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get report details from intent
        val studentName = intent.getStringExtra("STUDENT_NAME") ?: "Unknown Student"
        val studentId = intent.getStringExtra("STUDENT_ID") ?: "Unknown ID"
        val startDate = intent.getStringExtra("START_DATE") ?: "Unknown Date"
        val endDate = intent.getStringExtra("END_DATE") ?: "Unknown Date"
        val reportId = intent.getStringExtra("REPORT_ID") ?: "RPT${System.currentTimeMillis() % 10000}"
        val generateDate = intent.getStringExtra("GENERATE_DATE")
            ?: SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Calendar.getInstance().time)

        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentReportConfirmationScreen(
                        reportId = reportId,
                        studentName = studentName,
                        studentId = studentId,
                        startDate = startDate,
                        endDate = endDate,
                        generateDate = generateDate,
                        onBackToDashboard = {
                            // Navigate back to dashboard with clear flags
                            val intent = Intent(this, AdminDashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        },
                        onViewReport = {
                            // Just show a toast message for now instead of navigating
                            Toast.makeText(
                                this,
                                "Report viewing functionality coming soon!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentReportConfirmationScreen(
    reportId: String,
    studentName: String,
    studentId: String,
    startDate: String,
    endDate: String,
    generateDate: String,
    onBackToDashboard: () -> Unit,
    onViewReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section
        Spacer(modifier = Modifier.height(24.dp))

        // Middle section with confirmation and details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Success icon in circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6F3FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Report Generated",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation text
            Text(
                text = "Report Generated Successfully!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0795DD)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Report ID
            Text(
                text = "Report ID: $reportId",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Report summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Report Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0795DD),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ReportConfirmationDetailItem(
                        icon = Icons.Default.Person,
                        detail = "Student: $studentName"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ReportConfirmationDetailItem(
                        icon = Icons.Default.AccountBox,
                        detail = "Student ID: $studentId"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ReportConfirmationDetailItem(
                        icon = Icons.Default.CalendarToday,
                        detail = "Period: $startDate to $endDate"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ReportConfirmationDetailItem(
                        icon = Icons.Default.Schedule,
                        detail = "Generated on: $generateDate"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmation message
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
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF0795DD),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = "Your report has been successfully generated and is ready for viewing. You can also access this report later from the Reports section.",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Bottom section with buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Back to Dashboard Button
            OutlinedButton(
                onClick = onBackToDashboard,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0795DD)
                )
            ) {
                Text(
                    text = "Back to Dashboard",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // View Report Button
            Button(
                onClick = onViewReport,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0795DD),
                )
            ) {
                Text(
                    text = "View Report",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ReportConfirmationDetailItem(
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
            tint = Color(0xFF0795DD),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = detail,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
    }
}