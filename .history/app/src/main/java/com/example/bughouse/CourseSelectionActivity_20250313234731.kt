package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch

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

// Course data class
data class Course(
    val id: String,
    val name: String,
    val department: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSelectionScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Sample course data
    val allCourses = remember {
        listOf(
            Course("CSE 1301", "Introduction to Computing", "Computer Science", "Basic introduction to computer science concepts"),
            Course("CSE 1310", "Introduction to Computers & Programming", "Computer Science", "Introduction to problem solving with computers"),
            Course("CSE 1320", "Intermediate Programming", "Computer Science", "Continues CSE 1310 and provides more advanced programming"),
            Course("CSE 1325", "Object-Oriented Programming", "Computer Science", "Introduction to object-oriented programming concepts"),
            Course("CSE 2312", "Computer Organization & Assembly Language", "Computer Science", "Introduction to computer organization"),
            Course("CSE 2315", "Discrete Structures", "Computer Science", "Introduction to mathematical structures for computer science"),
            Course("CSE 3302", "Programming Languages", "Computer Science", "Study of programming language concepts"),
            Course("CSE 3310", "Fundamentals of Software Engineering", "Computer Science", "Software life cycle models"),
            Course("CSE 3311", "Object-Oriented Software Engineering", "Computer Science", "Study of object-oriented software engineering"),
            Course("CSE 3320", "Operating Systems", "Computer Science", "Functions and components of operating systems"),
            Course("CSE 3330", "Database Systems", "Computer Science", "Introduction to database management systems"),
            Course("CSE 3380", "Linear Algebra for CSE", "Computer Science", "Linear algebra concepts for computer science applications"),
            Course("MATH 1426", "Calculus I", "Mathematics", "Concepts of limit, continuity, differentiation and integration"),
            Course("MATH 2425", "Calculus II", "Mathematics", "Applications of integration, techniques of integration"),
            Course("PHYS 1443", "General Technical Physics I", "Physics", "Mechanics, heat, and sound")
        )
    }

    // Filtered courses based on search
    val filteredCourses = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            allCourses
        } else {
            allCourses.filter { course ->
                course.id.contains(searchQuery, ignoreCase = true) ||
                        course.name.contains(searchQuery, ignoreCase = true) ||
                        course.department.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawerContent(
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
                currentScreen = "Courses" // Mark this as the current screen (custom screen not in main navigation)
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
                        // Search suggestions could go here
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
                if (filteredCourses.isEmpty()) {
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
                } else {
                    // List of courses
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCourses) { course ->
                            CourseCard(
                                course = course,
                                onClick = {
                                    // Navigate to booking activity
                                    val intent = Intent(context, BookingActivity::class.java)
                                    intent.putExtra("COURSE_ID", course.id)
                                    intent.putExtra("COURSE_NAME", course.name)
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

@Composable
fun CourseCard(course: Course, onClick: () -> Unit) {
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