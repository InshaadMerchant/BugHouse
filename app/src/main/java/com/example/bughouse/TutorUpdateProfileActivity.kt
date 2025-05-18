package com.example.bughouse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import com.example.bughouse.components.TutorNavigationDrawer
import com.example.bughouse.DTO.TutorProfileData
import com.example.bughouse.DTO.TutorProfileUpdateRequest
import com.example.bughouse.APIService.ApiInterface
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutorUpdateProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorUpdateProfileScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorUpdateProfileScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Create API service directly to avoid ApiClient reference issues
    val apiService = RetrofitClient.retrofit.create(com.example.bughouse.APIService.ApiInterface::class.java)

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // States for form fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var netId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    // States for validation
    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    // State for loading
    var isLoading by remember { mutableStateOf(true) }

    // State for error message
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State for profile image selection
    var isImageSelected by remember { mutableStateOf(false) }

    // State for edit mode
    var isEditMode by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Mark that an image was selected, without trying to display it
        if (uri != null) {
            isImageSelected = true
        }
    }

    // Validation functions
    val validateFullName = { name: String ->
        name.isNotBlank()
    }

    val validateEmail = { email: String ->
        email.isNotBlank() && email.contains("@") && (email.endsWith(".edu") || email.endsWith(".com"))
    }

    // Function to fetch tutor profile data
    fun fetchProfileData() {
        isLoading = true
        errorMessage = null

        apiService.getTutorProfile(userId).enqueue(object : Callback<TutorProfileData> {
            override fun onResponse(call: Call<TutorProfileData>, response: Response<TutorProfileData>) {
                isLoading = false
                if (response.isSuccessful) {
                    val tutorProfileData = response.body()
                    tutorProfileData?.let {
                        fullName = it.fullName
                        email = it.email
                        netId = it.netId ?: ""
                        department = it.department ?: ""
                        isImageSelected = true // Assume user has profile image
                    }
                } else {
                    errorMessage = "Failed to load profile: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TutorProfileData>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    // Function to update profile data
    fun updateProfileData() {
        isLoading = true
        errorMessage = null

        val tutorProfileUpdateRequest = TutorProfileUpdateRequest(
            fullName = fullName,
            email = email,
            phoneNumber = null, // Keep null for these removed fields
            employeeId = null,
            netId = netId,
            department = department,
            numCoursesTeaching = null
        )

        apiService.updateTutorProfile(userId, tutorProfileUpdateRequest).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    isEditMode = false // Exit edit mode after successful save
                } else {
                    errorMessage = "Failed to update profile: ${response.message()}"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Fetch profile data when component is first rendered
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            fetchProfileData()
        } else {
            isLoading = false
            errorMessage = "User ID not found. Please log in again."
        }
    }

    TutorNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        currentScreen = "Profile"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Update Profile",
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
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
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
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Error message if any
                        if (errorMessage != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFB71C1C)
                                )
                            }
                        }

                        // Status indicator for edit mode
                        AnimatedVisibility(visible = isEditMode) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                            ) {
                                Text(
                                    text = "You're in edit mode. Make your changes and tap Save Changes.",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF7E6514)
                                )
                            }
                        }

                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(if (isImageSelected) Color(0xFF0795DD) else Color(0xFFE6F3FA))
                                .clickable(enabled = isEditMode) {
                                    if (isEditMode) {
                                        imagePickerLauncher.launch("image/*")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isImageSelected) {
                                // Generate initials from name if available
                                val initials = fullName.split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString()?.uppercase() }
                                    .joinToString("")

                                if (initials.isNotEmpty()) {
                                    Text(
                                        text = initials,
                                        color = Color.White,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Image",
                                        modifier = Modifier.size(50.dp),
                                        tint = Color.White
                                    )
                                }
                            } else {
                                // Show placeholder
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Upload Image",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color(0xFF0795DD)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Upload Photo",
                                        fontSize = 14.sp,
                                        color = Color(0xFF0795DD),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Form Fields in a Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Full Name Field
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = {
                                        if (isEditMode) {
                                            fullName = it
                                            fullNameError = !validateFullName(it)
                                        }
                                    },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = fullNameError,
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        errorBorderColor = Color.Red
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = isEditMode
                                )

                                if (fullNameError) {
                                    Text(
                                        text = "Please enter your full name",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Email Field
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        if (isEditMode) {
                                            email = it
                                            emailError = it.isNotEmpty() && !validateEmail(it)
                                        }
                                    },
                                    label = { Text("Email Address (.edu or .com)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = emailError,
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray,
                                        errorBorderColor = Color.Red
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = isEditMode
                                )

                                if (emailError) {
                                    Text(
                                        text = "Please enter a valid email address",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Net ID Field
                                OutlinedTextField(
                                    value = netId,
                                    onValueChange = {
                                        if (isEditMode) {
                                            netId = it
                                        }
                                    },
                                    label = { Text("Net ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = isEditMode
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Department Field
                                OutlinedTextField(
                                    value = department,
                                    onValueChange = {
                                        if (isEditMode) {
                                            department = it
                                        }
                                    },
                                    label = { Text("Department") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF0795DD),
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = isEditMode
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Save Changes Button (only visible in edit mode)
                        AnimatedVisibility(visible = isEditMode) {
                            Button(
                                onClick = {
                                    // Validate all fields
                                    fullNameError = !validateFullName(fullName)
                                    emailError = !validateEmail(email)

                                    val fieldsValid = !fullNameError && !emailError

                                    if (fieldsValid) {
                                        // Validation successful - save changes
                                        updateProfileData()
                                    } else {
                                        Toast.makeText(context, "Please fix the errors in the form", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0795DD))
                            ) {
                                Text(
                                    "Save Changes",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Back to Home Button (always visible)
                        Button(
                            onClick = {
                                // Navigate back to Dashboard
                                val intent = Intent(context, TutorDashboardActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditMode) Color.Gray else Color(0xFF0795DD)
                            )
                        ) {
                            Text(
                                "Back to Home",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}