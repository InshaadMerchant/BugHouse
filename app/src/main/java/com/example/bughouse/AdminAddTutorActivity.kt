package com.example.bughouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import java.io.Serializable

class AdminAddTutorActivity : ComponentActivity() {

    // Time slots state for each day
    private val scheduleData = mutableStateMapOf(
        "Monday" to mutableStateListOf<TimeSlot>(),
        "Tuesday" to mutableStateListOf<TimeSlot>(),
        "Wednesday" to mutableStateListOf<TimeSlot>(),
        "Thursday" to mutableStateListOf<TimeSlot>(),
        "Friday" to mutableStateListOf<TimeSlot>()
    )

    // Form state
    private val tutorName = mutableStateOf("")
    private val tutorEmail = mutableStateOf("")
    private val employeeNetId = mutableStateOf("")
    private val employeeId = mutableStateOf("")

    // Validation state
    private val nameError = mutableStateOf(false)
    private val emailError = mutableStateOf(false)
    private val netIdError = mutableStateOf(false)
    private val idError = mutableStateOf(false)

    // Selected day state
    private val selectedDay = mutableStateOf("Monday")

    // Register for activity result
    private val schedulingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val day = data.getStringExtra("SELECTED_DAY") ?: return@let
                val startTime = data.getStringExtra("START_TIME") ?: return@let
                val endTime = data.getStringExtra("END_TIME") ?: return@let

                // Add the new time slot
                val newTimeSlot = TimeSlot(startTime, endTime)
                scheduleData[day]?.add(newTimeSlot)
            }
        }
    }

    private fun removeTimeSlot(day: String, index: Int) {
        if (scheduleData.containsKey(day) && index >= 0 && index < scheduleData[day]?.size ?: 0) {
            scheduleData[day]?.removeAt(index)
        }
    }

    private fun launchSchedulingActivity() {
        // Validate required fields before allowing to add schedule
        if (!validateFields()) {
            Toast.makeText(
                this,
                "Please complete all required fields correctly",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(this, AdminTutorSchedulingActivity::class.java).apply {
            putExtra("TUTOR_NAME", tutorName.value)
            putExtra("TUTOR_EMAIL", tutorEmail.value)
            putExtra("SELECTED_DAY", selectedDay.value)
        }
        schedulingLauncher.launch(intent)
    }

    private fun validateFields(): Boolean {
        // Check name (required)
        nameError.value = tutorName.value.isBlank()

        // Check email (required and must end with .edu)
        emailError.value = !isValidEmail(tutorEmail.value)

        // Check netId (required)
        netIdError.value = employeeNetId.value.isBlank()

        // Check employee ID (required and must be 10 digits)
        idError.value = !isValidEmployeeId(employeeId.value)

        return !nameError.value && !emailError.value && !netIdError.value && !idError.value
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && email.endsWith(".edu") && email.contains("@")
    }

    private fun isValidEmployeeId(id: String): Boolean {
        return id.length == 10 && id.all { it.isDigit() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminAddTutorScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminAddTutorScreen() {
        val context = LocalContext.current
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

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
                    currentScreen = "Add Tutor"
                )
            },
            gesturesEnabled = true
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Add a Tutor",
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
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Text(
                            text = "Tutor Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Input Fields in a Card
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
                                // Tutor Name Field
                                OutlinedTextField(
                                    value = tutorName.value,
                                    onValueChange = {
                                        tutorName.value = it
                                        nameError.value = it.isBlank()
                                    },
                                    label = { Text("Tutor Name*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = nameError.value,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        focusedLabelColor = Color(0xFF0795DD),
                                        errorBorderColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (nameError.value) {
                                    Text(
                                        text = "Name is required",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Tutor Email Field
                                OutlinedTextField(
                                    value = tutorEmail.value,
                                    onValueChange = {
                                        tutorEmail.value = it
                                        emailError.value = !isValidEmail(it)
                                    },
                                    label = { Text("Tutor Email* (.edu)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = emailError.value,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        focusedLabelColor = Color(0xFF0795DD),
                                        errorBorderColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (emailError.value) {
                                    Text(
                                        text = "Valid .edu email is required",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Employee NetID Field
                                OutlinedTextField(
                                    value = employeeNetId.value,
                                    onValueChange = {
                                        employeeNetId.value = it
                                        netIdError.value = it.isBlank()
                                    },
                                    label = { Text("Employee NetID*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = netIdError.value,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        focusedLabelColor = Color(0xFF0795DD),
                                        errorBorderColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (netIdError.value) {
                                    Text(
                                        text = "NetID is required",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Employee ID Field
                                OutlinedTextField(
                                    value = employeeId.value,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() } && it.length <= 10) {
                                            employeeId.value = it
                                        }
                                        idError.value = !isValidEmployeeId(employeeId.value)
                                    },
                                    label = { Text("Employee ID* (10 digits)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = idError.value,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        focusedLabelColor = Color(0xFF0795DD),
                                        errorBorderColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (idError.value) {
                                    Text(
                                        text = "ID must be 10 digits",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Day Selection Section with inline Add button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Schedule",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0795DD)
                            )

                            IconButton(
                                onClick = { launchSchedulingActivity() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF0795DD), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Schedule",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display time slots or "No Added Time Slot" message in a Card
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
                                // Day selection
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Circular day buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        DayButton("M", "Monday", selectedDay)
                                        DayButton("T", "Tuesday", selectedDay)
                                        DayButton("W", "Wednesday", selectedDay)
                                        DayButton("T", "Thursday", selectedDay)
                                        DayButton("F", "Friday", selectedDay)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "${selectedDay.value} Time Slots",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                val timeSlots = scheduleData[selectedDay.value] ?: emptyList()

                                if (timeSlots.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No Added Time Slot",
                                            fontSize = 16.sp,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        timeSlots.forEachIndexed { index, timeSlot ->
                                            Box(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                // Time slot card with white background
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(end = 8.dp),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                    )
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp)
                                                    ) {
                                                        Text(
                                                            text = "${timeSlot.startTime} - ${timeSlot.endTime}",
                                                            fontSize = 16.sp,
                                                            color = Color(0xFF333333)
                                                        )
                                                    }
                                                }

                                                // Close button that appears to overlap the card
                                                Card(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.TopEnd)
                                                        .offset(x = (-4).dp, y = (-4).dp),
                                                    shape = CircleShape,
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color(0xFFFBE9E7)
                                                    ),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clickable {
                                                                removeTimeSlot(selectedDay.value, index)
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Remove",
                                                            tint = Color.Red,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Confirm Button - UPDATED to navigate to AdminTutorScheduleReviewActivity
                        Button(
                            onClick = {
                                // Validate before saving
                                if (validateFields()) {
                                    // Check if any schedules were added
                                    var hasSchedules = false
                                    for (slots in scheduleData.values) {
                                        if (slots.isNotEmpty()) {
                                            hasSchedules = true
                                            break
                                        }
                                    }

                                    if (!hasSchedules) {
                                        Toast.makeText(
                                            context,
                                            "Please add at least one schedule",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    // Navigate to schedule review screen
                                    val intent = Intent(context, AdminTutorScheduleReviewActivity::class.java).apply {
                                        putExtra("TUTOR_NAME", tutorName.value)
                                        putExtra("TUTOR_EMAIL", tutorEmail.value)
                                        putExtra("EMPLOYEE_NET_ID", employeeNetId.value)
                                        putExtra("EMPLOYEE_ID", employeeId.value)

                                        // Pass all schedule data
                                        for (day in scheduleData.keys) {
                                            val timeSlots = scheduleData[day]?.toList() ?: emptyList()
                                            putExtra("${day}_SLOTS", ArrayList(timeSlots))
                                        }
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please complete all required fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                        ) {
                            Text(
                                text = "Review Schedule",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info card about tutor scheduling
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
                                    text = "Add time slots for each day the tutor is available. You can add multiple slots per day if needed.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Add note about required fields
                        Text(
                            text = "* Required fields",
                            color = Color(0xFF666666),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DayButton(
        shortDay: String,
        fullDay: String,
        selectedDay: MutableState<String>
    ) {
        val isSelected = selectedDay.value == fullDay
        val backgroundColor = if (isSelected) Color(0xFF0795DD) else Color.White
        val textColor = if (isSelected) Color.White else Color(0xFF0795DD)

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.dp, Color(0xFF0795DD), CircleShape)
                .clickable { selectedDay.value = fullDay },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shortDay,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

// Data class for time slots
data class TimeSlot(
    val startTime: String,
    val endTime: String
) : Serializable