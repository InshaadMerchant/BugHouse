package com.example.bughouse

import ApiClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.DTO.CourseData
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CourseSelectionScreen()
                }
            }
        }
    }
}

// Student course model for UI (can be mapped from CourseData)
data class StudentCourse(
    val id: String,
    val name: String,
    val department: String,
    val description: String,
    val actualDatabaseId: Int // Add this field to store the actual course_id from the database
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSelectionScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State variables for course data
    var courses by remember { mutableStateOf<List<StudentCourse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Function to fetch courses from API
    fun fetchCourses() {
        isLoading = true
        error = null

        ApiClient.apiService.getCourses().enqueue(object : Callback<List<CourseData>> {
            override fun onResponse(call: Call<List<CourseData>>, response: Response<List<CourseData>>) {
                isLoading = false
                if (response.isSuccessful) {
                    // Map CourseData to StudentCourse
                    courses = response.body()?.map { course ->
                        // Extract department from course code (assuming format like "CS101")
                        val department = course.course_code.takeWhile { !it.isDigit() }

                        // Store the real database course_id in the StudentCourse model
                        StudentCourse(
                            id = course.course_code,
                            name = course.course_title,
                            department = department,
                            description = course.course_description ?: "",
                            actualDatabaseId = course.course_id // Use the actual course_id from database
                        )
                    } ?: emptyList()

                    // Log the courses we received for debugging
                    Log.d("CourseSelection", "Loaded ${courses.size} courses:")
                    courses.forEach { course ->
                        Log.d("CourseSelection", "Course: ${course.id}, Name: ${course.name}, DB ID: ${course.actualDatabaseId}")
                    }
                } else {
                    error = "Failed to load courses: ${response.message()}"
                    Log.e("CourseSelection", "API error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CourseData>>, t: Throwable) {
                isLoading = false
                error = "Network error: ${t.message}"
                Log.e("CourseSelection", "Error fetching courses", t)
            }
        })
    }

    // Fetch courses when component mounts
    LaunchedEffect(Unit) {
        fetchCourses()
    }

    // Filtered courses based on search
    val filteredCourses = remember(searchQuery, courses) {
        if (searchQuery.isEmpty()) {
            courses
        } else {
            courses.filter { course ->
                course.id.contains(searchQuery, ignoreCase = true) ||
                        course.name.contains(searchQuery, ignoreCase = true) ||
                        course.department.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainNavigationDrawerContent(
                onHomeClick = {
                    val intent = Intent(context, DashboardActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
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
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                },
                currentScreen = "Courses"
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                if (!isSearchActive) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Select a Course",
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
                            IconButton(onClick = { fetchCourses() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                            }

                            // Search button
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
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
                } else {
                    // Search bar appears when search is active
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { isSearchActive = false },
                        active = isSearchActive,
                        onActiveChange = { isSearchActive = it },
                        placeholder = { Text("Search courses...") },
                        leadingIcon = {
                            IconButton(onClick = { isSearchActive = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Search suggestions
                        LazyColumn {
                            items(
                                filteredCourses.take(5)
                            ) { course ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchQuery = course.id
                                            isSearchActive = false
                                        }
                                        .padding(16.dp)
                                ) {
                                    Text(text = "${course.id} - ${course.name}")
                                }
                            }
                        }
                    }
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
                when {
                    isLoading -> {
                        // Loading indicator
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF0795DD))
                        }
                    }
                    error != null -> {
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

                            androidx.compose.material3.Button(
                                onClick = { fetchCourses() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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
                    }
                    filteredCourses.isEmpty() && searchQuery.isNotEmpty() -> {
                        // Empty search results
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No courses found matching \"$searchQuery\"",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                    filteredCourses.isEmpty() && searchQuery.isEmpty() -> {
                        // No courses available
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No courses available",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        // List of courses
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredCourses) { course ->
                                StudentCourseCard(
                                    course = course,
                                    onClick = {
                                        // FIXED: Pass the actual database course ID directly
                                        Log.d("CourseSelection", "Selected course: ${course.id}, DB ID: ${course.actualDatabaseId}")

                                        val intent = Intent(context, BookingActivity::class.java)
                                        intent.putExtra("COURSE_ID", course.id)
                                        intent.putExtra("COURSE_NAME", course.name)
                                        intent.putExtra("ACTUAL_COURSE_ID", course.actualDatabaseId)
                                        context.startActivity(intent)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCourseCard(course: StudentCourse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0795DD)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Select",
                tint = Color(0xFF0795DD)
            )
        }
    }
}