package com.example.bughouse

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.bughouse.DTO.TutorAppointmentData
import com.example.bughouse.components.TutorNavigationDrawer
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "TutorManageAppointments"

class TutorManageAppointmentsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorNavigationDrawer(
                        drawerState = drawerState,
                        scope = scope,
                        currentScreen = "Upcoming Appointments"
                    ) {
                        TutorManageAppointmentsScreen(drawerState = drawerState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorManageAppointmentsScreen(drawerState: DrawerState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // Debug log the userId
    Log.d(TAG, "User ID from SharedPreferences: $userId")

    // Appointments state
    var upcomingAppointments = remember { mutableStateListOf<TutorAppointmentData>() }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Function to fetch tutor's appointments
    fun fetchAppointments() {
        if (userId.isEmpty()) {
            error = "User ID not found. Please log in again."
            isLoading = false
            Log.e(TAG, "Empty userId found in SharedPreferences")
            return
        }

        isLoading = true
        error = null
        Log.d(TAG, "Fetching appointments for tutor with userId: $userId")

        ApiClient.apiService.getTutorUpcomingAppointments(userId).enqueue(object : Callback<List<TutorAppointmentData>> {
            override fun onResponse(call: Call<List<TutorAppointmentData>>, response: Response<List<TutorAppointmentData>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val appointmentsList = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully retrieved ${appointmentsList.size} appointments")

                    upcomingAppointments.clear()
                    upcomingAppointments.addAll(appointmentsList)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    error = "Failed to load appointments (Error $errorCode): $errorBody"
                    Log.e(TAG, "API Error: $error")
                    Log.e(TAG, "Response headers: ${response.headers()}")
                }
            }

            override fun onFailure(call: Call<List<TutorAppointmentData>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
                Log.e(TAG, "Network failure", t)
            }
        })
    }

    // Function to cancel appointment
    fun cancelAppointment(appointmentId: Int, onSuccess: () -> Unit) {
        isLoading = true
        Log.d(TAG, "Attempting to cancel appointment with ID: $appointmentId")

        ApiClient.apiService.cancelAppointment(appointmentId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                isLoading = false
                if (response.isSuccessful) {
                    // Remove the appointment from the list
                    upcomingAppointments.removeIf { it.id == appointmentId }
                    Log.d(TAG, "Successfully cancelled appointment")
                    onSuccess()
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    error = "Failed to cancel appointment (Error $errorCode): $errorBody"
                    Log.e(TAG, "API Error during cancellation: $error")
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
                Log.e(TAG, "Network failure during cancellation", t)
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Fetch appointments when component mounts
    LaunchedEffect(userId) {
        fetchAppointments()
    }

    // State for confirmation dialog
    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf<TutorAppointmentData?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Upcoming Sessions",
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
                        text = "Failed to load appointments: ${error ?: "Unknown error"}",
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
            } else if (upcomingAppointments.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No upcoming tutoring sessions",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your schedule is currently clear",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // List of upcoming appointments to manage
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your Upcoming Tutoring Sessions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(upcomingAppointments) { appointment ->
                            TutorAppointmentCard(
                                appointment = appointment,
                                onCancelClick = {
                                    appointmentToCancel = appointment
                                    showCancelDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for cancelling appointment
    if (showCancelDialog && appointmentToCancel != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Session")
                }
            },
            text = {
                Text(
                    "Are you sure you want to cancel your tutoring session with ${appointmentToCancel?.tutorName} for ${appointmentToCancel?.courseName} on ${appointmentToCancel?.date} at ${appointmentToCancel?.time}? This will notify the student."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Process cancellation through the backend
                        appointmentToCancel?.id?.let { id ->
                            cancelAppointment(id) {
                                Toast.makeText(context, "Session cancelled successfully", Toast.LENGTH_SHORT).show()
                                showCancelDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Cancel Session")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text("Keep Session")
                }
            }
        )
    }
}

@Composable
fun TutorAppointmentCard(
    appointment: TutorAppointmentData,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0795DD))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Upcoming",
                    color = Color(0xFF0795DD),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Cancel button
                IconButton(
                    onClick = onCancelClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Session",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Student info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Student",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = appointment.tutorName, // This field contains student name for tutors
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Course info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Course",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = appointment.courseName,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = appointment.date,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${appointment.time} (${appointment.duration} mins)",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check if student has checked in
            if (appointment.checkedIn) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Student has checked in",
                        color = Color(0xFF43A047),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}