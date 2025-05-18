package com.example.bughouse

import ApiClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
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
import com.example.bughouse.DTO.ScheduleData
import com.example.bughouse.DTO.TutorData
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class BookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get course details from intent
        val courseId = intent.getStringExtra("COURSE_ID") ?: "Unknown Course"
        val courseName = intent.getStringExtra("COURSE_NAME") ?: "Unknown Course Name"
        val actualCourseId = intent.getIntExtra("ACTUAL_COURSE_ID", 1) // Default to 1 if not provided

        // Log received values for debugging
        Log.d("BookingActivity", "Received course: $courseId, $courseName, ID: $actualCourseId")

        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookingScreen(
                        courseId = courseId,
                        courseName = courseName,
                        actualCourseId = actualCourseId,
                        onBackPressed = { finish() },
                        onNavigateToConfirmation = { date, tutorId, tutorName, rating, timeSlot ->
                            val intent = Intent(this, ConfirmBookingActivity::class.java)
                            intent.putExtra("COURSE_ID", courseId)
                            intent.putExtra("COURSE_NAME", courseName)
                            intent.putExtra("ACTUAL_COURSE_ID", actualCourseId)
                            intent.putExtra("SELECTED_DATE", date)
                            intent.putExtra("TUTOR_ID", tutorId)
                            intent.putExtra("TUTOR_NAME", tutorName)
                            intent.putExtra("TUTOR_RATING", rating)
                            intent.putExtra("TIME_SLOT", timeSlot)
                            startActivity(intent)
                        },
                        onShowToast = { message ->
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

// Data class for TutorTimeSlot to better represent available times
data class TutorTimeSlot(
    val startTime: String,
    val formattedTime: String // Display friendly format
)

// Extension of TutorData to include available slots
data class TutorWithSchedule(
    val id: Int,
    val name: String,
    val rating: Double,
    val department: String,
    val availableTimeSlots: List<TutorTimeSlot> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    courseId: String,
    courseName: String,
    actualCourseId: Int,
    onBackPressed: () -> Unit,
    onNavigateToConfirmation: (date: String, tutorId: Int, tutorName: String, rating: Double, timeSlot: String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State for calendar
    val currentDate = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // State for tutors
    var tutors by remember { mutableStateOf<List<TutorData>>(emptyList()) }
    var selectedTutor by remember { mutableStateOf<TutorWithSchedule?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<TutorTimeSlot?>(null) }
    var isLoadingTutors by remember { mutableStateOf(false) }
    var isLoadingSchedules by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State to track API calls completion
    var schedulesRequestsCompleted by remember { mutableStateOf(0) }

    // Fetch tutors for the course
    LaunchedEffect(actualCourseId) {
        isLoadingTutors = true
        errorMessage = null

        Log.d("BookingActivity", "Fetching tutors for course ID: $actualCourseId")

        ApiClient.apiService.getTutorsByCourse(actualCourseId).enqueue(object : Callback<List<TutorData>> {
            override fun onResponse(call: Call<List<TutorData>>, response: Response<List<TutorData>>) {
                isLoadingTutors = false
                if (response.isSuccessful) {
                    tutors = response.body() ?: emptyList()
                    Log.d("BookingActivity", "Retrieved ${tutors.size} tutors: ${tutors.map { it.tutor_id }}")

                    if (tutors.isEmpty()) {
                        Log.d("BookingActivity", "No tutors found for this course")
                    }
                } else {
                    errorMessage = "Failed to load tutors: ${response.message()}"
                    Log.e("BookingActivity", "API error: ${response.code()} - ${response.message()}")
                    onShowToast(errorMessage ?: "An error occurred")
                }
            }

            override fun onFailure(call: Call<List<TutorData>>, t: Throwable) {
                isLoadingTutors = false
                errorMessage = "Network error: ${t.message}"
                Log.e("BookingActivity", "Error fetching tutors", t)
                onShowToast(errorMessage ?: "Network error")
            }
        })
    }

    // Map tutors to include schedule if a date is selected
    val tutorsWithSchedules = remember(tutors, selectedDate) {
        mutableStateListOf<TutorWithSchedule>()
    }

    // Load tutor schedules when a date is selected
    LaunchedEffect(selectedDate, tutors) {
        if (selectedDate != null && tutors.isNotEmpty()) {
            tutorsWithSchedules.clear()
            isLoadingSchedules = true
            schedulesRequestsCompleted = 0

            // Get day of week (1-7, where 1 is Monday)
            val dayOfWeek = selectedDate!!.dayOfWeek.value
            Log.d("BookingActivity", "Selected date: $selectedDate, Day of week: $dayOfWeek")

            // Process each tutor
            tutors.forEach { tutor ->
                Log.d("BookingActivity", "Fetching schedules for tutor ID: ${tutor.tutor_id}")

                ApiClient.apiService.getTutorSchedules(tutor.tutor_id).enqueue(object : Callback<List<ScheduleData>> {
                    override fun onResponse(call: Call<List<ScheduleData>>, response: Response<List<ScheduleData>>) {
                        schedulesRequestsCompleted++

                        if (response.isSuccessful) {
                            val schedules = response.body() ?: emptyList()
                            Log.d("BookingActivity", "Retrieved ${schedules.size} schedule entries for tutor ${tutor.tutor_id}")

                            // Filter schedules for the selected day
                            val daySchedules = schedules.filter {
                                it.day_of_week == dayOfWeek && it.is_available
                            }

                            Log.d("BookingActivity", "Found ${daySchedules.size} available slots for day $dayOfWeek")

                            // Create time slots
                            val timeSlots = daySchedules.map { schedule ->
                                val startTime = schedule.start_time
                                val formattedTime = formatTimeString(startTime)
                                TutorTimeSlot(startTime, formattedTime)
                            }

                            // Only add tutors who have available slots on the selected day
                            if (timeSlots.isNotEmpty()) {
                                tutorsWithSchedules.add(
                                    TutorWithSchedule(
                                        id = tutor.tutor_id,
                                        name = tutor.full_name,
                                        rating = tutor.rating,
                                        department = tutor.department,
                                        availableTimeSlots = timeSlots
                                    )
                                )
                                Log.d("BookingActivity", "Added tutor ${tutor.full_name} with ${timeSlots.size} slots")
                            }
                        } else {
                            Log.e("BookingActivity", "Error loading schedule: ${response.code()} - ${response.message()}")
                        }

                        // Check if all requests are complete
                        if (schedulesRequestsCompleted >= tutors.size) {
                            isLoadingSchedules = false
                            Log.d("BookingActivity", "All schedule requests completed. Found ${tutorsWithSchedules.size} tutors with available slots")
                        }
                    }

                    override fun onFailure(call: Call<List<ScheduleData>>, t: Throwable) {
                        schedulesRequestsCompleted++
                        Log.e("BookingActivity", "Network error loading schedule for tutor ${tutor.tutor_id}: ${t.message}", t)

                        // Check if all requests are complete
                        if (schedulesRequestsCompleted >= tutors.size) {
                            isLoadingSchedules = false
                            Log.d("BookingActivity", "All schedule requests completed (with some errors). Found ${tutorsWithSchedules.size} tutors with available slots")
                        }
                    }
                })
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = courseId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            text = courseName,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Calendar header with month navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month"
                        )
                    }
                }
            }

            item {
                // Calendar
                Calendar(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                        selectedTutor = null
                        selectedTimeSlot = null
                    }
                )
            }

            // Show loading indicator for tutors
            if (isLoadingTutors) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    CircularProgressIndicator(color = Color(0xFF0795DD))
                    Text(
                        text = "Loading tutors...",
                        modifier = Modifier.padding(top = 16.dp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            // Show loading indicator for schedules
            else if (isLoadingSchedules) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    CircularProgressIndicator(color = Color(0xFF0795DD))
                    Text(
                        text = "Loading schedules...",
                        modifier = Modifier.padding(top = 16.dp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            // Show error message if there is one
            else if (errorMessage != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "An error occurred",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            // Show tutors if date is selected and we have tutors with schedules
            else if (selectedDate != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Available Tutors",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Debug info for testers - can be removed in production
                item {
                    Text(
                        text = "Selected date: $selectedDate (Day ${selectedDate?.dayOfWeek?.value})",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Show message if no tutors are available on the selected day
                if (tutorsWithSchedules.isEmpty() && !isLoadingSchedules) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tutors available on the selected date",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Display tutors with available schedules
                items(tutorsWithSchedules) { tutor ->
                    TutorCard(
                        tutor = tutor,
                        isSelected = tutor == selectedTutor,
                        selectedTimeSlot = selectedTimeSlot,
                        onTutorSelected = { selectedTutor = tutor },
                        onTimeSlotSelected = { timeSlot -> selectedTimeSlot = timeSlot }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Show book button if a tutor and time slot are selected
                if (selectedTutor != null && selectedTimeSlot != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (selectedDate != null && selectedTutor != null && selectedTimeSlot != null) {
                                    onNavigateToConfirmation(
                                        selectedDate.toString(),
                                        selectedTutor!!.id,
                                        selectedTutor!!.name,
                                        selectedTutor!!.rating,
                                        selectedTimeSlot!!.startTime
                                    )
                                } else {
                                    onShowToast("Please select a date, tutor and time slot")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3F7FFF)
                            )
                        ) {
                            Text(
                                text = "Book this Appointment",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun Calendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp)
    ) {
        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            for (day in DayOfWeek.values()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()

        // Calculate the first day to display (previous month dates to fill first week)
        val firstDayOfGrid = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong() % 7)

        // Calculate how many weeks we need to display
        val totalDays = firstDayOfMonth.dayOfWeek.value % 7 + lastDayOfMonth.dayOfMonth
        val totalWeeks = (totalDays + 6) / 7 // Ceiling division to get number of weeks

        // Generate only the dates we need
        val allDates = generateSequence(firstDayOfGrid) { it.plusDays(1) }
            .take(totalWeeks * 7)
            .toList()

        val chunkedDates = allDates.chunked(7)

        chunkedDates.forEach { weekDates ->
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDates.forEach { date ->
                    val isCurrentMonth = date.month == currentMonth.month
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Color(0xFF0795DD)
                                    isToday -> Color(0xFFE3F2FD)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = isCurrentMonth) {
                                onDateSelected(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 14.sp,
                            color = when {
                                isSelected -> Color.White
                                !isCurrentMonth -> Color.LightGray
                                else -> Color.Black
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TutorCard(
    tutor: TutorWithSchedule,
    isSelected: Boolean,
    selectedTimeSlot: TutorTimeSlot?,
    onTutorSelected: () -> Unit,
    onTimeSlotSelected: (TutorTimeSlot) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onTutorSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F7FF) else Color.White
        ),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF3F7FFF)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tutor.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = tutor.department,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", tutor.rating),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tutor.availableTimeSlots) { timeSlot ->
                    val isTimeSelected = isSelected && timeSlot == selectedTimeSlot

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTimeSelected) Color(0xFF3F7FFF) else Color.White,
                        contentColor = if (isTimeSelected) Color.White else Color(0xFF3F7FFF),
                        border = BorderStroke(1.dp, Color(0xFF3F7FFF)),
                        modifier = Modifier.clickable {
                            onTutorSelected()
                            onTimeSlotSelected(timeSlot)
                        }
                    ) {
                        Text(
                            text = timeSlot.formattedTime,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to format time string from 24h format to 12h AM/PM format
fun formatTimeString(timeString: String): String {
    try {
        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val time = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return time.format(formatter)
    } catch (e: Exception) {
        Log.e("BookingActivity", "Error formatting time: $timeString", e)
        return timeString // Return original if parsing fails
    }
}