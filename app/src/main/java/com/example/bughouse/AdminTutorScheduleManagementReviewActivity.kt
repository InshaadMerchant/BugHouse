package com.example.bughouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

class AdminTutorScheduleManagementReviewActivity: ComponentActivity() {

    // Time slots state for display
    private val scheduleData = mutableStateMapOf(
        "Monday" to mutableStateListOf<TimeSlot>(),
        "Tuesday" to mutableStateListOf<TimeSlot>(),
        "Wednesday" to mutableStateListOf<TimeSlot>(),
        "Thursday" to mutableStateListOf<TimeSlot>(),
        "Friday" to mutableStateListOf<TimeSlot>()
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown Tutor"
        val tutorEmail = intent.getStringExtra("TUTOR_EMAIL") ?: ""
        val employeeId = intent.getStringExtra("EMPLOYEE_ID") ?: ""

        // Load schedule data from intent
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        days.forEach { day ->
            val timeSlots = intent.getSerializableExtra("${day}_SLOTS") as? ArrayList<TimeSlot>
            timeSlots?.let { slots ->
                scheduleData[day]?.clear()
                scheduleData[day]?.addAll(slots)
            }
        }

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReviewTutorScheduleScreen(
                        tutorName = tutorName,
                        tutorEmail = tutorEmail,
                        employeeId = employeeId
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ReviewTutorScheduleScreen(
        tutorName: String,
        tutorEmail: String,
        employeeId: String
    ) {
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Review Changes",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Tutor info card
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
                        Text(
                            text = tutorName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tutorEmail,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ID: $employeeId",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Schedule Calendar - read only
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
                        // Header with title
                        Text(
                            text = "Updated Schedule",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Day headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday").forEach { day ->
                                Text(
                                    text = day.take(3),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0795DD),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar grid with time markers
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            // Time markers at 4-hour intervals
                            val timeMarkers = listOf("12 AM", "4 AM", "8 AM", "12 PM", "4 PM", "8 PM")
                            val positions = listOf(0f, 0.167f, 0.333f, 0.5f, 0.667f, 0.833f)

                            timeMarkers.forEachIndexed { index, time ->
                                // Horizontal time marker line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.LightGray)
                                        .align(Alignment.TopStart)
                                        .offset(y = (positions[index] * 500).dp)
                                )

                                // Time label
                                Text(
                                    text = time,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(y = (positions[index] * 500 - 10).dp)
                                )
                            }

                            // Day columns with scheduled slots
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

                                days.forEachIndexed { dayIndex, day ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .padding(horizontal = 2.dp)
                                    ) {
                                        // Display each time slot in the day column
                                        scheduleData[day]?.forEachIndexed { index, slot ->
                                            val (startPosition, height) = calculateTimePosForEdit(slot)

                                            // Time slot card - read only
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(height.dp)
                                                    .offset(y = startPosition.dp)
                                                    .padding(1.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFE6F3FA)
                                                ),
                                                shape = RoundedCornerShape(4.dp),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(4.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = slot.startTime,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF0795DD),
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Summary of schedule
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
                        Text(
                            text = "Schedule Summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Summary of schedule
                        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday").forEach { day ->
                            val slots = scheduleData[day] ?: emptyList()

                            if (slots.isNotEmpty()) {
                                Text(
                                    text = day,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                slots.forEach { slot ->
                                    Text(
                                        text = "${slot.startTime} - ${slot.endTime}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666),
                                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        // In a real app, save changes to database
                        Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()

                        // Simulate navigation to admin dashboard
                        // In a real app, you would navigate to the AdminDashboardActivity
                        val intent = Intent(context, AdminDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                ) {
                    Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Calculate the vertical position and height of the time slot
    private fun calculateTimePosForEdit(slot: TimeSlot): Pair<Float, Float> {
        fun get24HourValue(timeStr: String): Float {
            val parts = timeStr.split(":")
            var hour = parts[0].toInt()
            val minutePart = parts[1].split(" ")
            val minute = minutePart[0].toInt()
            val amPm = minutePart[1]

            if (amPm == "PM" && hour != 12) {
                hour += 12
            } else if (amPm == "AM" && hour == 12) {
                hour = 0
            }

            return hour + (minute / 60f)
        }

        val startHour = get24HourValue(slot.startTime)
        val endHour = get24HourValue(slot.endTime)

        val totalHeight = 500f
        val startPosition = (startHour / 24f) * totalHeight
        val slotHeight = ((endHour - startHour) / 24f) * totalHeight

        return Pair(startPosition, maxOf(slotHeight, 20f))
    }
}