package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bughouse.APIService.ApiInterface
import com.example.bughouse.DTO.TutorAppointmentData
import com.example.bughouse.components.TutorNavigationDrawer
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

// Data class to hold the parsed date information
data class ParsedDate(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int
)

class TutorDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorDashboardScreen()
                }
            }
        }
    }
}

// Function to parse an appointment date string into components
fun parseAppointmentDate(dateString: String): ParsedDate {
    try {
        // Handle formats like "April 25, 2025"
        val parts = dateString.split(" ")
        val day = parts[1].replace(",", "").toInt()

        val monthMap = mapOf(
            "January" to 0, "February" to 1, "March" to 2, "April" to 3,
            "May" to 4, "June" to 5, "July" to 6, "August" to 7,
            "September" to 8, "October" to 9, "November" to 10, "December" to 11
        )

        val month = monthMap[parts[0]] ?: 0
        val year = parts[2].toInt()

        return ParsedDate(day, month, year)
    } catch (e: Exception) {
        // Default to current date on parsing error
        val calendar = Calendar.getInstance()
        return ParsedDate(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboardScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // State for appointments
    var appointments by remember { mutableStateOf<List<TutorAppointmentData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Selected day state
    var selectedDayNumber by remember { mutableStateOf<Int?>(null) }
    var selectedMonthNumber by remember { mutableStateOf<Int?>(null) }
    var selectedYearNumber by remember { mutableStateOf<Int?>(null) }
    var showDayAppointmentsDialog by remember { mutableStateOf(false) }

    // Function to fetch appointments
    fun fetchAppointments() {
        if (userId.isEmpty()) {
            errorMessage = "User ID not found. Please log in again."
            isLoading = false
            return
        }

        isLoading = true
        errorMessage = null

        ApiClient.apiService.getTutorAppointments(userId).enqueue(object : Callback<List<TutorAppointmentData>> {
            override fun onResponse(call: Call<List<TutorAppointmentData>>, response: Response<List<TutorAppointmentData>>) {
                isLoading = false
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                } else {
                    errorMessage = "Failed to load appointments: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<TutorAppointmentData>>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    // Fetch appointments when component mounts
    LaunchedEffect(userId) {
        fetchAppointments()
    }

    // Get all days that have scheduled appointments
    val daysWithAppointmentsList = remember(appointments) {
        // Filter appointments for scheduled status and current month/year
        val calendarInstance = Calendar.getInstance()
        val currentMonthValue = calendarInstance.get(Calendar.MONTH)
        val currentYearValue = calendarInstance.get(Calendar.YEAR)

        appointments
            .filter { appointment ->
                val parsedDate = parseAppointmentDate(appointment.date)
                appointment.status == "scheduled" &&
                        parsedDate.month == currentMonthValue &&
                        parsedDate.year == currentYearValue
            }
            .map { parseAppointmentDate(it.date).dayOfMonth }
            .distinct()
    }

    TutorNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        currentScreen = "Tutor Dashboard"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Tutor Dashboard",
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
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading) {
                    // Loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0795DD))
                    }
                } else if (errorMessage != null) {
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
                            text = errorMessage ?: "An unknown error occurred",
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
                            // Welcome message
                            Text(
                                text = "Welcome, Tutor!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0795DD),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Calendar Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                TutorCalendarView(
                                    modifier = Modifier.fillMaxWidth(),
                                    daysWithAppointments = daysWithAppointmentsList,
                                    onDaySelected = { day, month, year ->
                                        selectedDayNumber = day
                                        selectedMonthNumber = month
                                        selectedYearNumber = year
                                        showDayAppointmentsDialog = true
                                    }
                                )
                            }

                            // Manage Appointments Button with icon
                            Button(
                                onClick = {
                                    val intent = Intent(context, TutorManageAppointmentsActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
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
                            .sortedByDescending { it.date } // Sort by date string, should work since dates are formatted consistently
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
                                TutorAppointmentCardView(
                                    appointment = appointment,
                                    onClickAction = {
                                        // Navigate to AppointmentDetailsActivity with appointment details
                                        val intent = Intent(context, AppointmentDetailsActivity::class.java).apply {
                                            putExtra("APPOINTMENT_ID", appointment.id)
                                            putExtra("STUDENT_NAME", appointment.tutorName) // Using tutorName field which contains student name
                                            putExtra("COURSE_NAME", appointment.courseName)
                                            putExtra("APPOINTMENT_TIME", appointment.time)
                                            putExtra("APPOINTMENT_DATE", appointment.date)
                                            putExtra("STATUS", appointment.status)
                                            putExtra("IS_TUTOR_CONTEXT", true)
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

    // Day Appointments Dialog - Shows when a day with appointments is clicked
    if (showDayAppointmentsDialog && selectedDayNumber != null && selectedMonthNumber != null && selectedYearNumber != null) {
        // Filter appointments for the selected day, month, and year
        // Only show scheduled appointments in the calendar day view
        val dayAppointments = appointments.filter { appointment ->
            val parsedDate = parseAppointmentDate(appointment.date)
            parsedDate.dayOfMonth == selectedDayNumber &&
                    parsedDate.month == selectedMonthNumber &&
                    parsedDate.year == selectedYearNumber &&
                    appointment.status == "scheduled" // Only show scheduled appointments
        }

        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val calendarInstance = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYearNumber!!)
            set(Calendar.MONTH, selectedMonthNumber!!)
            set(Calendar.DAY_OF_MONTH, selectedDayNumber!!)
        }
        val formattedDate = dateFormat.format(calendarInstance.time)

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
                                            // Navigate to TutorCheckinActivity for appointments that haven't been checked in
                                            if (!appointment.checkedIn) {
                                                val intent = Intent(context, TutorCheckinActivity::class.java).apply {
                                                    putExtra("studentName", appointment.tutorName) // StudentName is in tutorName field
                                                    putExtra("courseCode", appointment.courseName)
                                                    putExtra("appointmentTime", appointment.time)
                                                    putExtra("appointmentDate", appointment.date)
                                                    putExtra("appointmentId", appointment.id)
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
                                                text = appointment.tutorName, // Student's name
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
fun TutorCalendarView(
    modifier: Modifier = Modifier,
    daysWithAppointments: List<Int>,
    onDaySelected: (Int, Int, Int) -> Unit
) {
    // Using Calendar for API level compatibility
    val calendarInstance = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

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

    Column(
        modifier = modifier
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
                                        onDaySelected(day, displayedMonth, displayedYear)
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

@Composable
fun TutorAppointmentCardView(
    appointment: TutorAppointmentData,
    onClickAction: () -> Unit
) {
    // Parse the appointment date to get components
    val parsedDate = parseAppointmentDate(appointment.date)

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
            .clickable(onClick = onClickAction),
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
                        calendar.set(Calendar.MONTH, parsedDate.month)
                        val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)

                        Text(
                            text = "$monthName ${parsedDate.dayOfMonth}",
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
                    text = appointment.tutorName, // Student's name
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
                            else -> appointment.status.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            }
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