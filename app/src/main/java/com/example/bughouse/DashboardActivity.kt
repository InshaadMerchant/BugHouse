package com.example.bughouse

import ApiClient
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import com.example.bughouse.DTO.DashboardAppointmentData
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val authObj = SingleAccountModeFragment(context=context)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // State for appointments
    var appointments by remember { mutableStateOf<List<DashboardAppointmentData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Selected day to view appointments
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var showDayAppointmentsDialog by remember { mutableStateOf(false) }

    // Function to fetch appointments
    fun fetchAppointments() {
        if (userId.isEmpty()) {
            error = "User ID not found. Please log in again."
            isLoading = false
            return
        }

        isLoading = true
        error = null

        ApiClient.apiService.getUserAppointments(userId).enqueue(object : Callback<List<DashboardAppointmentData>> {
            override fun onResponse(call: Call<List<DashboardAppointmentData>>, response: Response<List<DashboardAppointmentData>>) {
                isLoading = false
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                } else {
                    error = "Failed to load appointments: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<DashboardAppointmentData>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
            }
        })
    }

    // Fetch appointments when component mounts
    LaunchedEffect(userId) {
        fetchAppointments()
    }

    // Get all days that have scheduled appointments only
    val daysWithAppointments = remember(appointments) {
        // Filter appointments for scheduled status, current month and year
        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        appointments
            .filter {
                it.status == "scheduled" &&
                        it.month == currentMonth &&
                        it.year == currentYear
            }
            .map { it.dayOfMonth }
            .distinct()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainNavigationDrawerContent(
                onHomeClick = {
                    // Navigate to DashboardActivity (refresh current screen)
                    val intent = Intent(context, DashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    // Navigate to UpdateProfileActivity
                    val intent = Intent(context, UpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAppointmentsClick = {
                    val intent = Intent(context, YourAppointmentsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    val intent = Intent(context, ContactUsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    val intent = Intent(context, AboutActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    // Clear user data from SharedPreferences when logging out
                    with(sharedPreferences.edit()) {
                        clear()
                        apply()
                    }

                    // Navigate back to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    authObj.signOut()
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "Home" // Mark this as the Home screen
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "BugHouse",
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
                    actions = {
                        // Add refresh button
                        IconButton(onClick = { fetchAppointments() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0795DD),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    containerColor = Color(0xFF0795DD),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
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
                if (isLoading) {
                    // Loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0795DD))
                    }
                } else if (error != null) {
                    // Error state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = error ?: "An unknown error occurred",
                            fontSize = 18.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { fetchAppointments() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0795DD)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Try Again")
                        }
                    }
                } else {
                    // Content area with scrolling
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            // Calendar Section
                            ModernCalendarView(
                                modifier = Modifier.fillMaxWidth(),
                                daysWithAppointments = daysWithAppointments,
                                onDayClick = { day, month, year ->
                                    selectedDay = day
                                    selectedMonth = month
                                    selectedYear = year
                                    showDayAppointmentsDialog = true
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Manage Appointments Button with icon
                            Button(
                                onClick = {
                                    val intent = Intent(context, ManageAppointmentsActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    "Manage Appointments",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Recent Appointments Section (Completed appointments)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color(0xFF0795DD),
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Recent Appointments",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Show completed appointments in recent appointments section
                        val completedAppointments = appointments
                            .filter { it.status == "completed" || it.checkedIn }
                            .sortedByDescending { "${it.year}-${it.month+1}-${it.dayOfMonth}" }
                            .take(5)

                        if (completedAppointments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No completed appointments yet",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            items(completedAppointments) { appointment ->
                                ModernAppointmentCard(
                                    appointment = appointment,
                                    onClick = {
                                        // For completed appointments, navigate to ReviewActivity
                                        val intent = Intent(context, ReviewActivity::class.java).apply {
                                            putExtra("appointmentId", appointment.id)
                                            putExtra("tutorName", appointment.tutorName)
                                            putExtra("courseName", appointment.courseName)
                                            putExtra("appointmentTime", appointment.time)
                                            putExtra("appointmentDate", appointment.date)
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Schedule Appointment Dialog
    if (showScheduleDialog) {
        Dialog(onDismissRequest = { showScheduleDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Schedule Options",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            val intent = Intent(context, CourseSelectionActivity::class.java)
                            context.startActivity(intent)
                            showScheduleDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                    ) {
                        Text(
                            "Schedule an Appointment",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { showScheduleDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Day Appointments Dialog - Shows when a day with appointments is clicked
    if (showDayAppointmentsDialog && selectedDay != null && selectedMonth != null && selectedYear != null) {
        // Filter appointments for the selected day, month, and year
        // Only show scheduled appointments in the calendar day view
        val dayAppointments = appointments.filter {
            it.dayOfMonth == selectedDay &&
                    it.month == selectedMonth &&
                    it.year == selectedYear &&
                    it.status == "scheduled" // Only show scheduled appointments
        }

        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear!!)
            set(Calendar.MONTH, selectedMonth!!)
            set(Calendar.DAY_OF_MONTH, selectedDay!!)
        }
        val formattedDate = dateFormat.format(calendar.time)

        Dialog(onDismissRequest = { showDayAppointmentsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Appointments on $formattedDate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { showDayAppointmentsDialog = false }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // List of appointments for this day
                    if (dayAppointments.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            dayAppointments.forEach { appointment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            // Only navigate to CheckinActivity for scheduled appointments
                                            // that have not been checked in yet
                                            if (!appointment.checkedIn) {
                                                val intent = Intent(context, CheckinActivity::class.java).apply {
                                                    putExtra("appointmentId", appointment.id)
                                                    putExtra("tutorName", appointment.tutorName)
                                                    putExtra("courseName", appointment.courseName)
                                                    putExtra("appointmentTime", appointment.time)
                                                    putExtra("appointmentDate", appointment.date)
                                                }
                                                context.startActivity(intent)
                                                showDayAppointmentsDialog = false
                                            }
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFE)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Time column with circle background
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE6F3FA)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = appointment.time,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0795DD),
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Appointment details
                                        Column {
                                            Text(
                                                text = appointment.tutorName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = appointment.courseName,
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )

                                            // Status badge
                                            val statusColor = when {
                                                appointment.checkedIn -> Color(0xFF4CAF50)
                                                else -> Color(0xFF0795DD)
                                            }

                                            val statusText = when {
                                                appointment.checkedIn -> "Checked In"
                                                else -> "Scheduled"
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(statusColor.copy(alpha = 0.1f))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = statusText,
                                                    color = statusColor,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No scheduled appointments for this day",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCalendarView(
    modifier: Modifier = Modifier,
    daysWithAppointments: List<Int>,
    onDayClick: (Int, Int, Int) -> Unit
) {
    // Using Calendar for API level compatibility
    var calendarInstance = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    // State for current displayed month
    var currentCalendar by remember { mutableStateOf(calendarInstance) }

    // Current displayed month information
    val currentMonth = dateFormat.format(currentCalendar.time)
    val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Get the month and year of the current displayed calendar
    val displayedMonth = currentCalendar.get(Calendar.MONTH)
    val displayedYear = currentCalendar.get(Calendar.YEAR)

    // Calculate first day of month
    val firstDayCalendar = remember(currentCalendar) {
        (currentCalendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val firstDayOfWeek = firstDayCalendar.get(Calendar.DAY_OF_WEEK) - 1

    // Current day (highlight only if viewing current month)
    val today = Calendar.getInstance()
    val isCurrentMonth = remember(currentCalendar) {
        currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }
    val currentDay = today.get(Calendar.DAY_OF_MONTH)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Calendar header with navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous month button
                IconButton(
                    onClick = {
                        currentCalendar = (currentCalendar.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = Color(0xFF0795DD)
                    )
                }

                // Current month display
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF0795DD),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = currentMonth,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Next month button
                IconButton(
                    onClick = {
                        currentCalendar = (currentCalendar.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Month",
                        tint = Color(0xFF0795DD)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Weekday headers
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days grid
            // Calculate starting position for first day of month
            val startingPos = firstDayOfWeek

            // Generate calendar grid
            val numRows = (startingPos + daysInMonth + 6) / 7 // Calculate how many rows we need
            for (row in 0 until numRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col - startingPos

                        if (dayIndex < 0 || dayIndex >= daysInMonth) {
                            // Empty cell for days outside current month
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                // Empty space
                            }
                        } else {
                            val day = dayIndex + 1
                            val isToday = isCurrentMonth && day == currentDay

                            // Check if day has appointments
                            val hasAppointment = daysWithAppointments.contains(day)

                            // Determine background color
                            val backgroundColor = when {
                                isToday -> Color(0xFF0795DD)
                                hasAppointment -> Color(0xFFD1ECFF)
                                else -> Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(backgroundColor)
                                    .clickable {
                                        if (hasAppointment) {
                                            onDayClick(day, displayedMonth, displayedYear)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = if (isToday) Color.White else Color.Black,
                                    fontWeight = if (isToday || hasAppointment) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Today button
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    currentCalendar = Calendar.getInstance()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Today",
                    color = Color(0xFF0795DD),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernAppointmentCard(
    appointment: DashboardAppointmentData,
    onClick: () -> Unit
) {
    // Determine status color
    val statusColor = when {
        appointment.checkedIn -> Color(0xFF4CAF50)
        appointment.status == "scheduled" -> Color(0xFF0795DD)
        appointment.status == "cancelled" -> Color(0xFFE57373)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column with circle background
            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE6F3FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = appointment.time,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0795DD),
                            fontSize = 14.sp
                        )

                        // Get month name abbreviation
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.MONTH, appointment.month)
                        val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)

                        Text(
                            text = "$monthName ${appointment.dayOfMonth}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Appointment details
            Column {
                Text(
                    text = appointment.tutorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = appointment.courseName,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = appointment.date,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                // Status badge
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when {
                            appointment.checkedIn -> "Checked In"
                            else -> appointment.status.capitalize()
                        },
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Extension function to capitalize the first letter of a string
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}