package com.example.bughouse

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentReportGenerationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModernStudentReportGenerationScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernStudentReportGenerationScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Form fields state
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // Validation states
    var studentNameError by remember { mutableStateOf(false) }
    var studentIdError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }

    // Date picker setup
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    val startDatePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            startDate = dateFormatter.format(calendar.time)
            startDateError = false
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            endDate = dateFormatter.format(calendar.time)
            endDateError = false
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Validation functions
    val validateStudentName = { name: String ->
        name.isNotBlank()
    }

    val validateStudentId = { id: String ->
        id.length == 10 && id.isDigitsOnly()
    }

    val validateDates = { start: String, end: String ->
        if (start.isBlank() || end.isBlank()) {
            false
        } else {
            try {
                val startDt = dateFormatter.parse(start)
                val endDt = dateFormatter.parse(end)
                startDt != null && endDt != null && !endDt.before(startDt)
            } catch (e: Exception) {
                false
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminNavigationDrawerContent(
                onHomeClick = {
                    // Navigate to AdminDashboardActivity
                    val intent = Intent(context, AdminDashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    // Navigate to AdminUpdateProfileActivity
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
                    // Navigate back to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "" // Not a main screen in the drawer
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Generate Student Report",
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
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8FBFE),
                                Color(0xFFEDF6FC)
                            )
                        )
                    )
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
                                imageVector = Icons.Default.Description,
                                contentDescription = "Report",
                                tint = Color(0xFF0795DD),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Column {
                            Text(
                                text = "Student Report",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0795DD)
                            )
                            Text(
                                text = "Generate detailed student activity reports",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Report Form Card
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
                            // Student Information Section
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
                                    text = "Student Information",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0795DD)
                                )
                            }

                            // Student Name Field
                            OutlinedTextField(
                                value = studentName,
                                onValueChange = {
                                    studentName = it
                                    studentNameError = !validateStudentName(it)
                                },
                                label = { Text("Name of Student") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = studentNameError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = Color(0xFF0795DD)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (studentNameError) {
                                Text(
                                    text = "Please enter student name",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Student ID Field
                            OutlinedTextField(
                                value = studentId,
                                onValueChange = {
                                    if (it.length <= 10 && it.isDigitsOnly()) {
                                        studentId = it
                                        studentIdError = it.isNotEmpty() && !validateStudentId(it)
                                    }
                                },
                                label = { Text("Student ID # (10 digits)") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = studentIdError,
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = Color(0xFF0795DD)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (studentIdError) {
                                Text(
                                    text = "Please enter a valid 10-digit Student ID",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(24.dp))

                            // Date Range Section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = null,
                                    tint = Color(0xFF0795DD),
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.size(12.dp))

                                Text(
                                    text = "Report Date Range",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0795DD)
                                )
                            }

                            // Start Date Field with Calendar Picker
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = {
                                    startDate = it
                                },
                                label = { Text("Start Date") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = startDateError,
                                singleLine = true,
                                readOnly = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = Color(0xFF0795DD)
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { startDatePicker.show() }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Select Date",
                                            tint = Color(0xFF0795DD)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (startDateError) {
                                Text(
                                    text = "Please select a start date",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // End Date Field with Calendar Picker
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = {
                                    endDate = it
                                },
                                label = { Text("End Date") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = endDateError,
                                singleLine = true,
                                readOnly = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF0795DD),
                                    unfocusedBorderColor = Color.Gray,
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = Color(0xFF0795DD)
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { endDatePicker.show() }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Select Date",
                                            tint = Color(0xFF0795DD)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (endDateError) {
                                Text(
                                    text = "Please select an end date",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            if (startDate.isNotEmpty() && endDate.isNotEmpty() && !validateDates(startDate, endDate)) {
                                Text(
                                    text = "End date must be after start date",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Generate Report Button
                            Button(
                                onClick = {
                                    // Validate all fields
                                    studentNameError = !validateStudentName(studentName)
                                    studentIdError = !validateStudentId(studentId)
                                    startDateError = startDate.isEmpty()
                                    endDateError = endDate.isEmpty()

                                    val datesValid = validateDates(startDate, endDate)

                                    if (!studentNameError && !studentIdError && !startDateError && !endDateError && datesValid) {
                                        // Immediately navigate to confirmation screen without animation
                                        val intent = Intent(context, StudentReportConfirmationActivity::class.java).apply {
                                            putExtra("STUDENT_NAME", studentName)
                                            putExtra("STUDENT_ID", studentId)
                                            putExtra("START_DATE", startDate)
                                            putExtra("END_DATE", endDate)
                                            putExtra("REPORT_ID", "RPT${System.currentTimeMillis() % 10000}")
                                            putExtra("GENERATE_DATE", dateFormatter.format(Calendar.getInstance().time))
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please fix the errors in the form",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0795DD),
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text(
                                    "Generate Student Report",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info card about report generation
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
                                text = "Student reports include appointment history, attendance records, and tutor feedback for the specified date range.",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}