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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.bughouse.models.Appointment
import com.example.bughouse.models.AppointmentStatus

class TutorCalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val currentMonth = remember { YearMonth.now() }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Mock appointment data for the selected date
    val mockAppointments = remember {
        mapOf(
            // March Appointments (keep existing ones)
            LocalDate.now() to listOf(
                Appointment(
                    id = "1",
                    studentName = "John Smith",
                    courseCode = "CSE 1301",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    time = "10:00 AM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                ),
                Appointment(
                    id = "2",
                    studentName = "Emily Johnson",
                    courseCode = "CSE 1302",
                    date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    time = "1:00 PM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.now().plusDays(2) to listOf(
                Appointment(
                    id = "3",
                    studentName = "Michael Brown",
                    courseCode = "CSE 1303",
                    date = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    time = "11:30 AM",
                    duration = "1.5 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.now().plusDays(5) to listOf(
                Appointment(
                    id = "4",
                    studentName = "Sarah Davis",
                    courseCode = "CSE 1301",
                    date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    time = "9:00 AM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                ),
                Appointment(
                    id = "5",
                    studentName = "James Wilson",
                    courseCode = "CSE 1302",
                    date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    time = "3:30 PM",
                    duration = "2 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            // April Appointments
            LocalDate.of(2025, 4, 3) to listOf(
                Appointment(
                    id = "26",
                    studentName = "Alex Turner",
                    courseCode = "Mobile Development",
                    date = "April 3, 2025",
                    time = "10:00 AM",
                    duration = "1.5 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 5) to listOf(
                Appointment(
                    id = "27",
                    studentName = "Emma White",
                    courseCode = "AI Fundamentals",
                    date = "April 5, 2025",
                    time = "2:00 PM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 8) to listOf(
                Appointment(
                    id = "28",
                    studentName = "David Lee",
                    courseCode = "Cybersecurity",
                    date = "April 8, 2025",
                    time = "11:30 AM",
                    duration = "2 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 12) to listOf(
                Appointment(
                    id = "29",
                    studentName = "Sophie Chen",
                    courseCode = "Cloud Architecture",
                    date = "April 12, 2025",
                    time = "1:15 PM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 15) to listOf(
                Appointment(
                    id = "30",
                    studentName = "Ryan Miller",
                    courseCode = "DevOps Practices",
                    date = "April 15, 2025",
                    time = "3:30 PM",
                    duration = "1.5 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 18) to listOf(
                Appointment(
                    id = "31",
                    studentName = "Lisa Anderson",
                    courseCode = "Data Structures",
                    date = "April 18, 2025",
                    time = "9:45 AM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 22) to listOf(
                Appointment(
                    id = "32",
                    studentName = "Mark Thompson",
                    courseCode = "Algorithms",
                    date = "April 22, 2025",
                    time = "2:30 PM",
                    duration = "2 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 25) to listOf(
                Appointment(
                    id = "33",
                    studentName = "Rachel Green",
                    courseCode = "Software Testing",
                    date = "April 25, 2025",
                    time = "10:30 AM",
                    duration = "1 hour",
                    status = AppointmentStatus.SCHEDULED
                )
            ),
            LocalDate.of(2025, 4, 29) to listOf(
                Appointment(
                    id = "34",
                    studentName = "Chris Martin",
                    courseCode = "Project Management",
                    date = "April 29, 2025",
                    time = "1:00 PM",
                    duration = "1.5 hours",
                    status = AppointmentStatus.SCHEDULED
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        context.startActivity(Intent(context, TutorDashboardActivity::class.java))
                    }) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous month button
                    IconButton(
                        onClick = {
                            selectedMonth = selectedMonth.minusMonths(1)
                            selectedDate = selectedMonth.atDay(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowLeft,
                            contentDescription = "Previous Month",
                            tint = Color(0xFF0795DD)
                        )
                    }
                    
                    // Current month and year display
                    Text(
                        text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF333333)
                    )
                    
                    // Next month button
                    IconButton(
                        onClick = {
                            selectedMonth = selectedMonth.plusMonths(1)
                            selectedDate = selectedMonth.atDay(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = "Next Month",
                            tint = Color(0xFF0795DD)
                        )
                    }
                }
            }
            
            // Calendar grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Day of week headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val daysOfWeek = DayOfWeek.values()
                        for (dayOfWeek in daysOfWeek) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0795DD)
                                )
                            }
                        }
                    }
                    
                    // Calendar days
                    val days = getDaysInMonthGrid(selectedMonth)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(days) { day ->
                            val isSelected = day == selectedDate
                            val hasAppointments = mockAppointments.keys.contains(day)
                            val isCurrentMonth = day.month == selectedMonth.month
                            
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF0795DD)
                                            hasAppointments -> Color(0xFFE3F2FD)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (day == LocalDate.now()) 2.dp else 0.dp,
                                        color = if (day == LocalDate.now()) Color(0xFFFF9800) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (isCurrentMonth) {
                                            selectedDate = day
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    color = when {
                                        isSelected -> Color.White
                                        !isCurrentMonth -> Color.LightGray
                                        else -> Color(0xFF333333)
                                    },
                                    fontWeight = if (isSelected || day == LocalDate.now()) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            
            // Appointments for selected date
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Appointments for ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0795DD)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (mockAppointments.containsKey(selectedDate)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(mockAppointments[selectedDate]!!) { appointment ->
                                AppointmentItem(appointment = appointment)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "No Appointments",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "No appointments for this date",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: Appointment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE6F3FA),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = appointment.studentName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = appointment.courseCode,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = appointment.time,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

// Helper function to generate calendar days grid
fun getDaysInMonthGrid(yearMonth: YearMonth): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    
    // Get the first day of the month
    val firstOfMonth = yearMonth.atDay(1)
    
    // Find the first day of the grid (might be in the previous month)
    val startOfGrid = firstOfMonth.minusDays(firstOfMonth.dayOfWeek.value.toLong() % 7)
    
    // Add 42 days (6 weeks) to ensure a full grid
    for (i in 0..41) {
        days.add(startOfGrid.plusDays(i.toLong()))
        
        // If we've reached the end of the month and gone into the next month, break
        if (days.size > 28 && days.last().month != yearMonth.month && days.last().dayOfWeek == DayOfWeek.SUNDAY) {
            break
        }
    }
    
    return days
} 