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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import java.io.Serializable

class AdminEditTutorScheduleLogicActivity : ComponentActivity() {

    // Time slots state that can be modified
    private val scheduleData = mutableStateMapOf(
        "Monday" to mutableStateListOf<TimeSlot>(),
        "Tuesday" to mutableStateListOf<TimeSlot>(),
        "Wednesday" to mutableStateListOf<TimeSlot>(),
        "Thursday" to mutableStateListOf<TimeSlot>(),
        "Friday" to mutableStateListOf<TimeSlot>()
    )

    // Selected day for adding new time slot
    private val selectedDay = mutableStateOf("Monday")

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
                    EditTutorScheduleScreen(
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
    private fun EditTutorScheduleScreen(
        tutorName: String,
        tutorEmail: String,
        employeeId: String
    ) {
        val context = LocalContext.current
        var selectedTimeSlot by remember { mutableStateOf<Triple<String, TimeSlot, Int>?>(null) }
        var showAddTimeSlotDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Manage Schedule",
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

                // Schedule Calendar with Add button
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
                        // Header with title and add button
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

                            // Add time slot button
                            FloatingActionButton(
                                onClick = { showAddTimeSlotDialog = true },
                                modifier = Modifier.size(40.dp),
                                containerColor = Color(0xFF0795DD),
                                contentColor = Color.White
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Time Slot",
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

                                            // Time slot card
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(height.dp)
                                                    .offset(y = startPosition.dp)
                                                    .padding(1.dp)
                                                    .clickable {
                                                        selectedTimeSlot = Triple(day, slot, index)
                                                    },
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

                // Confirm Changes Button
                Button(
                    onClick = {
                        // Create intent to navigate to review activity
                        val intent = Intent(context, AdminTutorScheduleManagementReviewActivity::class.java)

                        // Pass tutor info
                        intent.putExtra("TUTOR_NAME", tutorName)
                        intent.putExtra("TUTOR_EMAIL", tutorEmail)
                        intent.putExtra("EMPLOYEE_ID", employeeId)

                        // Pass all schedule data
                        for (day in scheduleData.keys) {
                            val timeSlots = scheduleData[day]?.toList() ?: emptyList()
                            intent.putExtra("${day}_SLOTS", ArrayList(timeSlots))
                        }

                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                ) {
                    Text("Confirm Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Show edit time slot dialog when a slot is selected
        if (selectedTimeSlot != null) {
            EditTimeSlotDialog(
                day = selectedTimeSlot!!.first,
                timeSlot = selectedTimeSlot!!.second,
                onDismiss = { selectedTimeSlot = null },
                onSave = { updatedSlot ->
                    val day = selectedTimeSlot!!.first
                    val index = selectedTimeSlot!!.third

                    scheduleData[day]?.set(index, updatedSlot)
                    selectedTimeSlot = null
                },
                onDelete = {
                    val day = selectedTimeSlot!!.first
                    val index = selectedTimeSlot!!.third

                    scheduleData[day]?.removeAt(index)
                    selectedTimeSlot = null
                }
            )
        }

        // Show add time slot dialog
        if (showAddTimeSlotDialog) {
            AddTimeSlotDialog(
                onDismiss = { showAddTimeSlotDialog = false },
                onAdd = { day, newSlot ->
                    scheduleData[day]?.add(newSlot)
                    showAddTimeSlotDialog = false
                }
            )
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EditTimeSlotDialog(
        day: String,
        timeSlot: TimeSlot,
        onDismiss: () -> Unit,
        onSave: (TimeSlot) -> Unit,
        onDelete: () -> Unit
    ) {
        // State for current time values
        var startTime by remember { mutableStateOf(timeSlot.startTime) }
        var endTime by remember { mutableStateOf(timeSlot.endTime) }

        // State for time pickers
        var showStartTimePicker by remember { mutableStateOf(false) }
        var showEndTimePicker by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Time Slot",
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start Time Section
                    Text(
                        text = "Start Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0795DD)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Start Time Button
                    Button(
                        onClick = { showStartTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = startTime,
                            color = Color(0xFF0795DD),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // End Time Section
                    Text(
                        text = "End Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0795DD)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // End Time Button
                    Button(
                        onClick = { showEndTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = endTime,
                            color = Color(0xFF0795DD),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Delete button
                        Button(
                            onClick = onDelete,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Delete")
                        }

                        // Save button
                        Button(
                            onClick = {
                                onSave(TimeSlot(startTime, endTime))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        // Time Picker Dialogs
        if (showStartTimePicker) {
            TimePickerDialog(
                initialTime = startTime,
                onDismiss = { showStartTimePicker = false },
                onTimeSelected = {
                    startTime = it
                    showStartTimePicker = false
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                initialTime = endTime,
                onDismiss = { showEndTimePicker = false },
                onTimeSelected = {
                    endTime = it
                    showEndTimePicker = false
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AddTimeSlotDialog(
        onDismiss: () -> Unit,
        onAdd: (String, TimeSlot) -> Unit
    ) {
        var selectedDay by remember { mutableStateOf("Monday") }

        // State for time values
        var startTime by remember { mutableStateOf("9:00 AM") }
        var endTime by remember { mutableStateOf("10:00 AM") }

        // State for time pickers
        var showStartTimePicker by remember { mutableStateOf(false) }
        var showEndTimePicker by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add New Time Slot",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0795DD),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Day selection
                    Text(
                        text = "Select Day",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0795DD)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DayButton("M", "Monday", selectedDay) { selectedDay = it }
                        DayButton("T", "Tuesday", selectedDay) { selectedDay = it }
                        DayButton("W", "Wednesday", selectedDay) { selectedDay = it }
                        DayButton("T", "Thursday", selectedDay) { selectedDay = it }
                        DayButton("F", "Friday", selectedDay) { selectedDay = it }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start Time Section
                    Text(
                        text = "Start Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0795DD)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Start Time Button
                    Button(
                        onClick = { showStartTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = startTime,
                            color = Color(0xFF0795DD),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // End Time Section
                    Text(
                        text = "End Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0795DD)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // End Time Button
                    Button(
                        onClick = { showEndTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F3FA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = endTime,
                            color = Color(0xFF0795DD),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Cancel button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Cancel")
                        }

                        // Add button
                        Button(
                            onClick = {
                                onAdd(selectedDay, TimeSlot(startTime, endTime))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        // Time Picker Dialogs
        if (showStartTimePicker) {
            TimePickerDialog(
                initialTime = startTime,
                onDismiss = { showStartTimePicker = false },
                onTimeSelected = {
                    startTime = it
                    showStartTimePicker = false
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                initialTime = endTime,
                onDismiss = { showEndTimePicker = false },
                onTimeSelected = {
                    endTime = it
                    showEndTimePicker = false
                }
            )
        }
    }

    @Composable
    private fun DayButton(
        shortDay: String,
        fullDay: String,
        selectedDay: String,
        onDaySelected: (String) -> Unit
    ) {
        val isSelected = selectedDay == fullDay
        val backgroundColor = if (isSelected) Color(0xFF0795DD) else Color.White
        val textColor = if (isSelected) Color.White else Color(0xFF0795DD)

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.dp, Color(0xFF0795DD), CircleShape)
                .clickable { onDaySelected(fullDay) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shortDay,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
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
    private fun parseTime(timeString: String): Triple<String, String, String> {
        val parts = timeString.split(":")
        val hourPart = parts[0]

        val minuteAndPeriod = parts[1].split(" ")
        val minutePart = minuteAndPeriod[0]
        val periodPart = minuteAndPeriod[1]

        return Triple(hourPart, minutePart, periodPart)
    }
} 