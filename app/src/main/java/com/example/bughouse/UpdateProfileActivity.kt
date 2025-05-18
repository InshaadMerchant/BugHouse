package com.example.bughouse

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.bughouse.APIService.ApiInterface
import com.example.bughouse.DTO.ProfileData
import com.example.bughouse.DTO.ProfileUpdateRequest
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UpdateProfileScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""
    val userRole = sharedPreferences.getString("userRole", "") ?: ""

    // States for form fields
    var fullName by remember { mutableStateOf("") }
    var utaId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // States for validation
    var fullNameError by remember { mutableStateOf(false) }
    var utaIdError by remember { mutableStateOf(false) }
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
    ) { uri: Uri? ->
        // Just mark that an image was selected, without trying to display it
        if (uri != null) {
            isImageSelected = true
        }
    }

    // Validation functions
    val validateFullName = { name: String ->
        name.isNotBlank()
    }

    val validateUtaId = { id: String ->
        id.length == 10 && id.isDigitsOnly()
    }

    val validateEmail = { email: String ->
        email.isNotBlank() && email.contains("@") && email.endsWith(".com")
    }

    // Function to fetch profile data
    fun fetchProfileData() {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getProfile(userId).enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                isLoading = false
                if (response.isSuccessful) {
                    val profileData = response.body()
                    profileData?.let {
                        fullName = it.fullName
                        email = it.email
                        utaId = it.studentIdNumber ?: ""
                        isImageSelected = true // Assume user has profile image
                    }
                } else {
                    errorMessage = "Failed to load profile: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    // Function to update profile data
    fun updateProfileData() {
        isLoading = true
        errorMessage = null

        val profileUpdateRequest = ProfileUpdateRequest(
            fullName = fullName,
            email = email,
            studentIdNumber = if (userRole == "student") utaId else null
        )

        ApiClient.apiService.updateProfile(userId, profileUpdateRequest).enqueue(object : Callback<Map<String, String>> {
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileNavigationDrawer(
                onHomeClick = {
                    // Navigate to DashboardActivity (refresh current screen)
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
                            text = "Profile",
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

                                // Only show UTA ID for students
                                if (userRole == "student") {
                                    // UTA ID Field
                                    OutlinedTextField(
                                        value = utaId,
                                        onValueChange = {
                                            if (isEditMode && it.length <= 10 && it.isDigitsOnly()) {
                                                utaId = it
                                                utaIdError = it.isNotEmpty() && !validateUtaId(it)
                                            }
                                        },
                                        label = { Text("UTA ID (10 digits)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = utaIdError,
                                        singleLine = true,
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color(0xFF0795DD),
                                            unfocusedBorderColor = Color.Gray,
                                            errorBorderColor = Color.Red
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                        enabled = isEditMode
                                    )

                                    if (utaIdError) {
                                        Text(
                                            text = "UTA ID must be exactly 10 digits",
                                            color = Color.Red,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // Email Field
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        if (isEditMode) {
                                            email = it
                                            emailError = it.isNotEmpty() && !validateEmail(it)
                                        }
                                    },
                                    label = { Text("Email Address (.edu)") },
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
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = isEditMode
                                )

                                if (emailError) {
                                    Text(
                                        text = "Please enter a valid .edu email address",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Save Changes Button (only visible in edit mode)
                        AnimatedVisibility(visible = isEditMode) {
                            Button(
                                onClick = {
                                    // Validate all fields
                                    fullNameError = !validateFullName(fullName)
                                    if (userRole == "student") {
                                        utaIdError = !validateUtaId(utaId)
                                    }
                                    emailError = !validateEmail(email)

                                    val fieldsValid = !fullNameError &&
                                            !(userRole == "student" && utaIdError) &&
                                            !emailError

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
                                val intent = Intent(context, DashboardActivity::class.java)
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

@Composable
fun ProfileNavigationDrawer(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
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
                        text = "JD",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "John Doe",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "john@mavs.uta.edu",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items with proper icons - highlight the Profile option
        ProfileNavMenuItem(
            icon = Icons.Default.Home,
            title = "Home",
            onClick = onHomeClick
        )

        ProfileNavMenuItem(
            icon = Icons.Default.Person,
            title = "Profile",
            onClick = onProfileClick,
            isSelected = true // This is the Profile page
        )

        ProfileNavMenuItem(
            icon = Icons.Default.DateRange,
            title = "Your Appointments",
            onClick = onAppointmentsClick
        )

        ProfileNavMenuItem(
            icon = Icons.Default.Email,
            title = "Contact Us",
            onClick = onContactUsClick
        )

        ProfileNavMenuItem(
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
        ProfileNavMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            onClick = onLogoutClick,
            tint = Color(0xFFE53935) // Red color for logout
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileNavMenuItem(
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