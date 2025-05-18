package com.example.bughouse

import ApiClient
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.DTO.AppointmentData
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class YourAppointmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppointmentsScreen()
                }
            }
        }
    }
}

// Appointment status enum
enum class AppointmentStatus {
    UPCOMING, COMPLETED, CANCELLED;

    companion object {
        fun fromString(status: String): AppointmentStatus {
            return when (status.lowercase()) {
                "scheduled" -> UPCOMING
                "completed" -> COMPLETED
                "cancelled" -> CANCELLED
                else -> UPCOMING
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // State for appointments
    var appointments by remember { mutableStateOf<List<AppointmentData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Function to fetch appointments
    fun fetchAppointments() {
        if (userId.isEmpty()) {
            error = "User ID not found. Please log in again."
            isLoading = false
            return
        }

        isLoading = true
        error = null

        ApiClient.apiService.getAppointments(userId).enqueue(object : Callback<List<AppointmentData>> {
            override fun onResponse(call: Call<List<AppointmentData>>, response: Response<List<AppointmentData>>) {
                isLoading = false
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                } else {
                    error = "Failed to load appointments: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<AppointmentData>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
            }
        })
    }

    // Fetch appointments when the component mounts
    LaunchedEffect(userId) {
        fetchAppointments()
    }

    // Filter appointments based on selected tab
    val filteredAppointments = appointments.filter { appointment ->
        val status = when (selectedTabIndex) {
            0 -> AppointmentStatus.UPCOMING
            1 -> AppointmentStatus.COMPLETED
            2 -> AppointmentStatus.CANCELLED
            else -> null
        }

        status == null || AppointmentStatus.fromString(appointment.status) == status
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppointmentsNavDrawer(
                onHomeClick = {
                    // Navigate to DashboardActivity
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
                    // Already on this screen, just close drawer
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
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Appointments",
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
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0795DD))
                    }
                } else if (error != null) {
                    // Show error message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error ?: "",
                                color = Color.Red,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                onClick = { fetchAppointments() },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF0795DD)
                                ),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Retry",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Tab Row for filtering appointments
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = Color(0xFF0795DD)
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                text = { Text("Upcoming") }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                text = { Text("Completed") }
                            )
                            Tab(
                                selected = selectedTabIndex == 2,
                                onClick = { selectedTabIndex = 2 },
                                text = { Text("Cancelled") }
                            )
                        }

                        // Appointments List
                        if (filteredAppointments.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(filteredAppointments) { appointment ->
                                    AppointmentCard(appointment = appointment)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        } else {
                            // Empty state
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No ${
                                        when (selectedTabIndex) {
                                            0 -> "upcoming"
                                            1 -> "completed"
                                            else -> "cancelled"
                                        }
                                    } appointments found",
                                    color = Color.Gray,
                                    fontSize = 18.sp
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
fun AppointmentsNavDrawer(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Get user info from SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val fullName = sharedPreferences.getString("fullName", "User") ?: "User"
    val email = sharedPreferences.getString("email", "") ?: ""

    // Generate initials
    val initials = fullName.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.toString()?.uppercase() }
        .joinToString("")

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
    ) {
        // Header with user info and avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0795DD))
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {
            Column {
                // Profile image with initials
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.ifEmpty { "U" },
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = fullName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = email,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items with proper icons
        AppointmentsNavMenuItem(
            icon = Icons.Default.Home,
            title = "Home",
            onClick = onHomeClick
        )

        AppointmentsNavMenuItem(
            icon = Icons.Default.Person,
            title = "Profile",
            onClick = onProfileClick
        )

        AppointmentsNavMenuItem(
            icon = Icons.Default.DateRange,
            title = "Your Appointments",
            onClick = onAppointmentsClick,
            isSelected = true // Highlight this option since we're in the appointments screen
        )

        AppointmentsNavMenuItem(
            icon = Icons.Default.Email,
            title = "Contact Us",
            onClick = onContactUsClick
        )

        AppointmentsNavMenuItem(
            icon = Icons.Default.Info,
            title = "About",
            onClick = onAboutClick
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.LightGray
        )

        // Logout with icon
        AppointmentsNavMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            onClick = onLogoutClick,
            tint = Color(0xFFE53935) // Red color for logout
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AppointmentsNavMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    tint: Color = Color(0xFF0795DD)
) {
    val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF0795DD) else Color.DarkGray
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentData) {
    val status = AppointmentStatus.fromString(appointment.status)

    val statusColor = when (status) {
        AppointmentStatus.UPCOMING -> Color(0xFF0795DD)
        AppointmentStatus.COMPLETED -> Color(0xFF4CAF50)
        AppointmentStatus.CANCELLED -> Color(0xFFE57373)
    }

    val statusText = when (status) {
        AppointmentStatus.UPCOMING -> "Upcoming"
        AppointmentStatus.COMPLETED -> "Completed"
        AppointmentStatus.CANCELLED -> "Cancelled"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                        .background(statusColor)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Appointment ID
                Text(
                    text = "ID: ${appointment.id}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Tutor info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Tutor",
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = appointment.tutorName,
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
                    imageVector = Icons.Default.School,
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
                    imageVector = Icons.Default.CalendarMonth,
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
                    imageVector = Icons.Default.AccessTime,
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

            if (status == AppointmentStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Completed session indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Session completed",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}