package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calendar
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TutorAttendanceLogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AttendanceLogsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceLogsScreen() {
    val context = LocalContext.current
    
    // Search filters
    var studentName by remember { mutableStateOf("") }
    var courseId by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf("") }
    
    // Mock attendance data - in a real app, this would come from an API
    val mockAttendanceData = remember {
        listOf(
            AttendanceLog(
                studentName = "John Smith",
                courseCode = "CSE 1301",
                date = "May 10, 2025",
                status = AttendanceStatus.ATTENDED
            ),
            AttendanceLog(
                studentName = "Emily Johnson",
                courseCode = "CSE 1302",
                date = "May 11, 2025",
                status = AttendanceStatus.MISSED
            ),
            AttendanceLog(
                studentName = "Michael Brown",
                courseCode = "CSE 1303",
                date = "May 12, 2025",
                status = AttendanceStatus.ATTENDED
            ),
            AttendanceLog(
                studentName = "Sarah Davis",
                courseCode = "CSE 1301",
                date = "May 13, 2025",
                status = AttendanceStatus.ATTENDED
            ),
            AttendanceLog(
                studentName = "James Wilson",
                courseCode = "CSE 1302",
                date = "May 14, 2025",
                status = AttendanceStatus.MISSED
            )
        )
    }
    
    // Filtered data based on search parameters
    val filteredData = mockAttendanceData.filter { log ->
        (studentName.isEmpty() || log.studentName.contains(studentName, ignoreCase = true)) &&
        (courseId.isEmpty() || log.courseCode.contains(courseId, ignoreCase = true)) &&
        (dateFilter.isEmpty() || log.date.contains(dateFilter, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance Logs",
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
            // Search filters section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Search Filters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0795DD)
                    )
                    
                    // Student Name Filter
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Student") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            focusedLabelColor = Color(0xFF0795DD),
                            cursorColor = Color(0xFF0795DD)
                        )
                    )
                    
                    // Course ID Filter
                    OutlinedTextField(
                        value = courseId,
                        onValueChange = { courseId = it },
                        label = { Text("Course ID/Name") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = "Course") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            focusedLabelColor = Color(0xFF0795DD),
                            cursorColor = Color(0xFF0795DD)
                        )
                    )
                    
                    // Date Filter
                    OutlinedTextField(
                        value = dateFilter,
                        onValueChange = { dateFilter = it },
                        label = { Text("Date (MM/DD/YYYY)") },
                        leadingIcon = { Icon(Icons.Default.Calendar, contentDescription = "Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            focusedLabelColor = Color(0xFF0795DD),
                            cursorColor = Color(0xFF0795DD)
                        )
                    )
                    
                    // Search Button
                    Button(
                        onClick = { /* Search is automatically applied as user types */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Search")
                    }
                }
            }
            
            // Results header
            Text(
                text = "Results (${filteredData.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF333333)
            )
            
            // Results list
            if (filteredData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No attendance logs match your search criteria",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredData) { attendanceLog ->
                        AttendanceLogItem(attendanceLog = attendanceLog)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceLogItem(attendanceLog: AttendanceLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (attendanceLog.status == AttendanceStatus.ATTENDED) 
                Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .background(
                        color = if (attendanceLog.status == AttendanceStatus.ATTENDED)
                            Color(0xFF4CAF50) else Color(0xFFE57373),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (attendanceLog.status == AttendanceStatus.ATTENDED)
                        Icons.Default.Check else Icons.Default.Clear,
                    contentDescription = "Attendance Status",
                    tint = Color.White,
                    modifier = Modifier.padding(4.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Attendance details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attendanceLog.studentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = attendanceLog.courseCode,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = attendanceLog.date,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            // Status text
            Text(
                text = if (attendanceLog.status == AttendanceStatus.ATTENDED) "Attended" else "Missed",
                color = if (attendanceLog.status == AttendanceStatus.ATTENDED)
                    Color(0xFF1B5E20) else Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// Data model for attendance logs
data class AttendanceLog(
    val studentName: String,
    val courseCode: String,
    val date: String,
    val status: AttendanceStatus
)

// Enum for attendance status
enum class AttendanceStatus {
    ATTENDED,
    MISSED
} 