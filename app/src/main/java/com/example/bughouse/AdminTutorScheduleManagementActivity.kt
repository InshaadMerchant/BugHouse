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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch

class AdminTutorScheduleManagementActivity : ComponentActivity() {

    // Mock tutor schedule data
    private val tutorSchedules = mapOf(
        "Tutor 1" to mapOf(
            "Monday" to listOf(TimeSlot("9:00 AM", "11:00 AM"), TimeSlot("2:00 PM", "4:00 PM")),
            "Tuesday" to listOf(TimeSlot("10:00 AM", "12:00 PM")),
            "Wednesday" to listOf(TimeSlot("1:00 PM", "3:00 PM")),
            "Thursday" to listOf(TimeSlot("11:00 AM", "1:00 PM"), TimeSlot("3:00 PM", "5:00 PM")),
            "Friday" to listOf(TimeSlot("9:00 AM", "12:00 PM"))
        ),
        "Tutor 2" to mapOf(
            "Monday" to listOf(TimeSlot("8:00 AM", "10:00 AM")),
            "Wednesday" to listOf(TimeSlot("11:00 AM", "1:00 PM")),
            "Friday" to listOf(TimeSlot("2:00 PM", "5:00 PM"))
        ),
        "Tutor 3" to mapOf(
            "Tuesday" to listOf(TimeSlot("9:00 AM", "12:00 PM")),
            "Thursday" to listOf(TimeSlot("1:00 PM", "4:00 PM"))
        ),
        "Tutor 4" to mapOf(
            "Monday" to listOf(TimeSlot("1:00 PM", "3:00 PM")),
            "Wednesday" to listOf(TimeSlot("9:00 AM", "11:00 AM")),
            "Friday" to listOf(TimeSlot("10:00 AM", "12:00 PM"))
        ),
        "Tutor 5" to mapOf(
            "Tuesday" to listOf(TimeSlot("2:00 PM", "4:00 PM")),
            "Thursday" to listOf(TimeSlot("9:00 AM", "11:00 AM"))
        ),
        "Tutor 6" to mapOf(
            "Monday" to listOf(TimeSlot("10:00 AM", "12:00 PM")),
            "Wednesday" to listOf(TimeSlot("2:00 PM", "4:00 PM")),
            "Friday" to listOf(TimeSlot("11:00 AM", "1:00 PM"))
        )
    )

    // Mock tutor email and employee ID data
    private val tutorDetails = mapOf(
        "Tutor 1" to Pair("tutor1@university.edu", "1234567890"),
        "Tutor 2" to Pair("tutor2@university.edu", "2345678901"),
        "Tutor 3" to Pair("tutor3@university.edu", "3456789012"),
        "Tutor 4" to Pair("tutor4@university.edu", "4567890123"),
        "Tutor 5" to Pair("tutor5@university.edu", "5678901234"),
        "Tutor 6" to Pair("tutor6@university.edu", "6789012345")
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get selected tutor from intent
        val selectedTutor = intent.getStringExtra("SELECTED_TUTOR") ?: "Tutor 1"

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AdminNavigationDrawerContent(
                                onHomeClick = {
                                    val intent = Intent(context, AdminDashboardActivity::class.java)
                                    context.startActivity(intent)
                                    scope.launch { drawerState.close() }
                                },
                                onProfileClick = {
                                    val intent = Intent(context, AdminUpdateProfileActivity::class.java)
                                    context.startActivity(intent)
                                    scope.launch { drawerState.close() }
                                },
                                onContactUsClick = {
                                    val intent = Intent(context, AdminContactUsActivity::class.java)
                                    context.startActivity(intent)
                                    scope.launch { drawerState.close() }
                                },
                                onAboutClick = {
                                    val intent = Intent(context, AdminAboutActivity::class.java)
                                    context.startActivity(intent)
                                    scope.launch { drawerState.close() }
                                },
                                onLogoutClick = {
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                },
                                currentScreen = "Tutor Schedule Management"
                            )
                        },
                        gesturesEnabled = true
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = "Tutor Schedule Management",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures { _, dragAmount ->
                                            when {
                                                dragAmount > 50 -> scope.launch {
                                                    if (drawerState.isClosed) drawerState.open()
                                                }
                                                dragAmount < -50 -> scope.launch {
                                                    if (drawerState.isOpen) drawerState.close()
                                                }
                                            }
                                        }
                                    }
                            ) {
                                TutorScheduleContent(
                                    tutorName = selectedTutor,
                                    tutorEmail = tutorDetails[selectedTutor]?.first ?: "",
                                    employeeId = tutorDetails[selectedTutor]?.second ?: "",
                                    scheduleData = tutorSchedules[selectedTutor] ?: emptyMap()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TutorScheduleContent(
        tutorName: String,
        tutorEmail: String,
        employeeId: String,
        scheduleData: Map<String, List<TimeSlot>>
    ) {
        val context = LocalContext.current
        var selectedSlot by remember { mutableStateOf<Pair<String, TimeSlot>?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
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

            // Weekly schedule card with edit icon
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
                    // Header row with title and edit icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Schedule",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD)
                        )

                        // Edit icon moved here
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Edit Tutor Schedule", Toast.LENGTH_SHORT).show()
                                // In a real app, navigate to edit screen
                                val intent = Intent(context, AdminEditTutorScheduleLogicActivity::class.java).apply {
                                    putExtra("TUTOR_NAME", tutorName)
                                    putExtra("TUTOR_EMAIL", tutorEmail)
                                    putExtra("EMPLOYEE_ID", employeeId)

                                    // Pass schedule data
                                    for (day in scheduleData.keys) {
                                        val timeSlots = scheduleData[day]?.toList() ?: emptyList()
                                        putExtra("${day}_SLOTS", ArrayList(timeSlots))
                                    }
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Schedule",
                                tint = Color(0xFF0795DD),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

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

                            days.forEach { day ->
                                ManagementDayColumn(
                                    day = day,
                                    timeSlots = scheduleData[day] ?: emptyList(),
                                    onSlotClick = { slot -> selectedSlot = Pair(day, slot) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Return to Dashboard Button
            Button(
                onClick = {
                    // Navigate back to dashboard
                    val intent = Intent(context, AdminDashboardActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
            ) {
                Text("Return to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Show time slot detail dialog if a slot is selected
        if (selectedSlot != null) {
            ManagementTimeSlotDetailDialog(
                day = selectedSlot!!.first,
                timeSlot = selectedSlot!!.second,
                onDismiss = { selectedSlot = null }
            )
        }
    }

    @Composable
    private fun ManagementDayColumn(
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
                val (startPosition, height) = calculateTimePosForManagement(slot)

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
    private fun calculateTimePosForManagement(slot: TimeSlot): Pair<Float, Float> {
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
    private fun ManagementTimeSlotDetailDialog(
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
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}