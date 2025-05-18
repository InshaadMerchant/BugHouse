package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

class AdminTutorScheduleReviewActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve data from intent
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown"
        val tutorEmail = intent.getStringExtra("TUTOR_EMAIL") ?: ""

        // Get schedule data - in a real app this would come from a database or serialized data
        val scheduleData = mutableMapOf<String, List<TimeSlot>>()
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

        for (day in days) {
            val timeSlots = intent.getSerializableExtra("${day}_SLOTS") as? ArrayList<TimeSlot>
            scheduleData[day] = timeSlots ?: emptyList()
        }

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Review Tutor Schedule",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back"
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
                    ScheduleReviewScreen(
                        tutorName = tutorName,
                        tutorEmail = tutorEmail,
                        scheduleData = scheduleData,
                        paddingValues = paddingValues,
                        onConfirm = {
                            // Navigate to admin dashboard
                            val intent = Intent(
                                this@AdminTutorScheduleReviewActivity,
                                AdminDashboardActivity::class.java
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            Toast.makeText(
                                this@AdminTutorScheduleReviewActivity,
                                "Tutor schedule confirmed",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleReviewScreen(
    tutorName: String,
    tutorEmail: String,
    scheduleData: Map<String, List<TimeSlot>>,
    paddingValues: PaddingValues,
    onConfirm: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedSlot by remember { mutableStateOf<Pair<String, TimeSlot>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tutor info header
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$tutorName's Schedule",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0795DD)
                )

                if (tutorEmail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tutorEmail,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calendar view
        WeekCalendarView(
            scheduleData = scheduleData,
            onSlotClick = { day, slot ->
                selectedSlot = Pair(day, slot)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Confirm button
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0795DD)
            )
        ) {
            Text(
                text = "Confirm Schedule",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Show time slot detail dialog if a slot is selected
    if (selectedSlot != null) {
        TimeSlotDetailDialog(
            day = selectedSlot!!.first,
            timeSlot = selectedSlot!!.second,
            onDismiss = { selectedSlot = null }
        )
    }
}

@Composable
fun WeekCalendarView(
    scheduleData: Map<String, List<TimeSlot>>,
    onSlotClick: (String, TimeSlot) -> Unit
) {
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
            // Calendar header
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
                // Time markers - Updated to show even time slots at 4-hour intervals
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

                    days.forEach { day ->
                        DayColumn(
                            day = day,
                            timeSlots = scheduleData[day] ?: emptyList(),
                            onSlotClick = { slot -> onSlotClick(day, slot) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayColumn(
    day: String,
    timeSlots: List<TimeSlot>,
    onSlotClick: (TimeSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp)
    ) {
        // Display each time slot in the day column
        timeSlots.forEach { slot ->
            val (startPosition, height) = calculateTimePosition(slot)

            // Time slot card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.dp)
                    .offset(y = startPosition.dp)
                    .padding(1.dp)
                    .clickable { onSlotClick(slot) },
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
                        text = "${slot.startTime}",
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

// Calculate the vertical position and height of the time slot
fun calculateTimePosition(slot: TimeSlot): Pair<Float, Float> {
    // Create a proper 24-hour time representation from the AM/PM format
    fun get24HourValue(timeStr: String): Float {
        val parts = timeStr.split(":")
        var hour = parts[0].toInt()
        val minutePart = parts[1].split(" ")
        val minute = minutePart[0].toInt()
        val amPm = minutePart[1]

        // Convert to 24-hour format
        if (amPm == "PM" && hour != 12) {
            hour += 12
        } else if (amPm == "AM" && hour == 12) {
            hour = 0
        }

        return hour + (minute / 60f)
    }

    // Parse the time strings directly to get 24-hour values
    val startHour = get24HourValue(slot.startTime)
    val endHour = get24HourValue(slot.endTime)

    // Map 24 hours to 500dp height
    val totalHeight = 500f
    val startPosition = (startHour / 24f) * totalHeight
    val slotHeight = ((endHour - startHour) / 24f) * totalHeight

    return Pair(startPosition, maxOf(slotHeight, 20f)) // Minimum height of 20dp for visibility
}

@Composable
fun TimeSlotDetailDialog(
    day: String,
    timeSlot: TimeSlot,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Changed to center alignment
            ) {
                Text(
                    text = "Time Slot Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0795DD),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Day: $day",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Time: ${timeSlot.startTime} - ${timeSlot.endTime}",
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD)),
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
                ) {
                    Text("Close")
                }
            }
        }
    }
}