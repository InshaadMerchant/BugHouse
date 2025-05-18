package com.example.bughouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch

class AdminTutorSchedulingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: ""
        val tutorEmail = intent.getStringExtra("TUTOR_EMAIL") ?: ""
        val selectedDay = intent.getStringExtra("SELECTED_DAY") ?: "Monday"

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminTutorSchedulingScreen(
                        tutorName = tutorName,
                        tutorEmail = tutorEmail,
                        selectedDay = selectedDay
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTutorSchedulingScreen(
    tutorName: String,
    tutorEmail: String,
    selectedDay: String
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // State for time slots
    val startTime = remember { mutableStateOf("9:00 AM") }
    val endTime = remember { mutableStateOf("10:00 AM") }

    // State for time pickers
    val showStartTimePicker = remember { mutableStateOf(false) }
    val showEndTimePicker = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add to Schedule",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
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
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8FBFE),
                                Color(0xFFEDF6FC)
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Circular icon background
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6F3FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Schedule",
                            tint = Color(0xFF0795DD),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Column {
                        Text(
                            text = "$selectedDay Schedule",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD)
                        )
                        Text(
                            text = "Add time slots for tutor availability",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Tutor Info Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF0795DD),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "Tutor Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0795DD)
                            )
                        }

                        Text(
                            text = "Name: $tutorName",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "Email: $tutorEmail",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Time Selection
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF0795DD),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "Select Time Slot",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0795DD)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Time Selection Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Start Time
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Start Time",
                                    fontSize = 16.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Button(
                                    onClick = { showStartTimePicker.value = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = startTime.value,
                                        color = Color(0xFF0795DD),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // End Time
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "End Time",
                                    fontSize = 16.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Button(
                                    onClick = { showEndTimePicker.value = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = endTime.value,
                                        color = Color(0xFF0795DD),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Confirm Button
                Button(
                    onClick = {
                        // Create a result intent with the time slot data
                        val resultIntent = Intent().apply {
                            putExtra("SELECTED_DAY", selectedDay)
                            putExtra("START_TIME", startTime.value)
                            putExtra("END_TIME", endTime.value)
                        }

                        // Set the result to pass data back to the calling activity
                        activity?.setResult(Activity.RESULT_OK, resultIntent)

                        Toast.makeText(context, "Schedule added", Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                ) {
                    Text(
                        text = "Confirm Schedule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info card
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
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF0795DD),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = "Time slots will be added to the tutor's schedule. Students will be able to book appointments during these time slots.",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Time Picker Dialogs
            if (showStartTimePicker.value) {
                TimePickerDialog(
                    initialTime = startTime.value,
                    onDismiss = { showStartTimePicker.value = false },
                    onTimeSelected = {
                        startTime.value = it
                        showStartTimePicker.value = false
                    }
                )
            }

            if (showEndTimePicker.value) {
                TimePickerDialog(
                    initialTime = endTime.value,
                    onDismiss = { showEndTimePicker.value = false },
                    onTimeSelected = {
                        endTime.value = it
                        showEndTimePicker.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    // Parse initial time
    val (hour, minute, period) = parseTime(initialTime)

    // State for time picker
    val selectedHour = remember { mutableStateOf(hour) }
    val selectedMinute = remember { mutableStateOf(minute) }
    val selectedPeriod = remember { mutableStateOf(period) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0795DD)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Time selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    TimePickerWheel(
                        items = (1..12).map { it.toString().padStart(2, '0') },
                        selectedItem = selectedHour,
                        label = "Hour"
                    )

                    Text(
                        text = ":",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    // Minute selector
                    TimePickerWheel(
                        items = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"),
                        selectedItem = selectedMinute,
                        label = "Minute"
                    )

                    // AM/PM selector
                    TimePickerWheel(
                        items = listOf("AM", "PM"),
                        selectedItem = selectedPeriod,
                        label = "Period"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF0795DD)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val formattedTime = "${selectedHour.value}:${selectedMinute.value} ${selectedPeriod.value}"
                            onTimeSelected(formattedTime)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerWheel(
    items: List<String>,
    selectedItem: MutableState<String>,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .height(120.dp)
                .width(80.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        val currentIndex = items.indexOf(selectedItem.value)
                        val newIndex = (currentIndex - 1 + items.size) % items.size
                        selectedItem.value = items[newIndex]
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Up",
                        tint = Color(0xFF0795DD)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedItem.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0795DD)
                    )
                }

                IconButton(
                    onClick = {
                        val currentIndex = items.indexOf(selectedItem.value)
                        val newIndex = (currentIndex + 1) % items.size
                        selectedItem.value = items[newIndex]
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Down",
                        tint = Color(0xFF0795DD)
                    )
                }
            }
        }
    }
}

// Utility function to parse time string
fun parseTime(timeString: String): Triple<String, String, String> {
    val parts = timeString.split(":")
    val hourPart = parts[0]

    val minuteAndPeriod = parts[1].split(" ")
    val minutePart = minuteAndPeriod[0]
    val periodPart = minuteAndPeriod[1]

    return Triple(hourPart, minutePart, periodPart)
}