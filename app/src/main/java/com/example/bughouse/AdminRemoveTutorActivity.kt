package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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

class AdminRemoveTutorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Changed from MaterialTheme.colorScheme.background to white
                ) {
                    RemoveTutorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveTutorScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    // Navigate to admin profile screen
                    val intent = Intent(context, AdminUpdateProfileActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onContactUsClick = {
                    // Navigate to contact us screen
                    val intent = Intent(context, AdminContactUsActivity::class.java)
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    // Navigate to about screen
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
                currentScreen = "Remove Tutor" // Mark as the current screen
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Remove a Tutor",
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
                RemoveTutorContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveTutorContent() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State for tutor selection dropdown
    var expandedTutor by remember { mutableStateOf(false) }
    var selectedTutor by remember { mutableStateOf("Select a Tutor") }
    val tutors = listOf(
        "Tutor 1", "Tutor 2", "Tutor 3", "Tutor 4", "Tutor 5",
        "Tutor 6", "Tutor 7", "Tutor 8", "Tutor 9", "Tutor 10"
    )

    // State for reason selection dropdown
    var expandedReason by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("Select a Reason") }
    val reasonOptions = listOf(
        "Graduation",
        "Leaving for another job",
        "Relocated",
        "Fired (Poor Performance)",
        "Fired (Policy Violation)",
        "Sabbatical",
        "Medical Leave",
        "End of Contract",
        "Other"
    )

    // State for other reason text field
    var otherReason by remember { mutableStateOf("") }

    // Validation states
    var isTutorSelected by remember { mutableStateOf(false) }
    var isReasonSelected by remember { mutableStateOf(false) }
    var isOtherReasonProvided by remember { mutableStateOf(true) } // Default to true, we'll validate as needed

    // Show error dialog state
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White) // Changed from default gray to white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Remove Tutor Form",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0795DD)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tutor Selection Dropdown with increased height
                ExposedDropdownMenuBox(
                    expanded = expandedTutor,
                    onExpandedChange = { expandedTutor = it }
                ) {
                    OutlinedTextField(
                        value = selectedTutor,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTutor)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            unfocusedBorderColor = Color(0xFF9E9E9E)
                        ),
                        label = { Text("Select Tutor") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp) // Increased height from default
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedTutor,
                        onDismissRequest = { expandedTutor = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        tutors.forEach { tutor ->
                            DropdownMenuItem(
                                text = { Text(text = tutor) },
                                onClick = {
                                    selectedTutor = tutor
                                    isTutorSelected = true
                                    expandedTutor = false
                                },
                                modifier = Modifier.height(56.dp) // Increased height for dropdown items
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reason for Removal Dropdown with increased height
                ExposedDropdownMenuBox(
                    expanded = expandedReason,
                    onExpandedChange = { expandedReason = it }
                ) {
                    OutlinedTextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReason)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            unfocusedBorderColor = Color(0xFF9E9E9E)
                        ),
                        label = { Text("Reason for Removal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp) // Increased height from default
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedReason,
                        onDismissRequest = { expandedReason = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        reasonOptions.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(text = reason) },
                                onClick = {
                                    selectedReason = reason
                                    isReasonSelected = true
                                    expandedReason = false

                                    // Reset other reason if not "Other"
                                    if (reason != "Other") {
                                        otherReason = ""
                                    }
                                },
                                modifier = Modifier.height(56.dp) // Increased height for dropdown items
                            )
                        }
                    }
                }

                // Other Reason Text Field (only visible if "Other" is selected) with increased height
                if (selectedReason == "Other") {
                    OutlinedTextField(
                        value = otherReason,
                        onValueChange = {
                            otherReason = it
                            isOtherReasonProvided = it.isNotEmpty()
                        },
                        label = { Text("Please specify reason") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp), // Increased height from default
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0795DD),
                            unfocusedBorderColor = Color(0xFF9E9E9E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Remove Tutor Button with increased height
                Button(
                    onClick = {
                        // Validate form
                        when {
                            !isTutorSelected || selectedTutor == "Select a Tutor" -> {
                                errorMessage = "Please select a tutor"
                                showErrorDialog = true
                            }

                            !isReasonSelected || selectedReason == "Select a Reason" -> {
                                errorMessage = "Please select a reason for removal"
                                showErrorDialog = true
                            }

                            selectedReason == "Other" && otherReason.isEmpty() -> {
                                errorMessage = "Please specify the reason for removal"
                                showErrorDialog = true
                            }

                            else -> {
                                // Form is valid, proceed to summary
                                val finalReason =
                                    if (selectedReason == "Other") otherReason else selectedReason

                                // Create intent and pass data
                                val intent = Intent(
                                    context,
                                    AdminRemoveTutorSummaryActivity::class.java
                                ).apply {
                                    putExtra("TUTOR_NAME", selectedTutor)
                                    putExtra("REASON", finalReason)
                                    putExtra("REASON_CATEGORY", selectedReason)
                                    putExtra(
                                        "REMOVAL_ID",
                                        "RM${System.currentTimeMillis() % 10000}"
                                    )
                                    putExtra(
                                        "REMOVAL_DATE", java.text.SimpleDateFormat(
                                            "MM/dd/yyyy",
                                            java.util.Locale.US
                                        ).format(java.util.Calendar.getInstance().time)
                                    )
                                }
                                context.startActivity(intent)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp), // Increased height from 56.dp to 64.dp
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0795DD)
                    )
                ) {
                    Text(
                        "Remove Tutor",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Info card about tutor removal (now with white background)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F3FA)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF0795DD),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                Text(
                    text = "Removing a tutor will revoke their access to the system and notify administrators. This action cannot be undone directly.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Validation Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0795DD)
                    ),
                    modifier = Modifier.height(48.dp) // Increased height for dialog button
                ) {
                    Text("OK")
                }
            }
        )
    }
}