package com.example.bughouse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.bughouse.ui.theme.BugHouseTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen() {
    val context = LocalContext.current

    // States for form fields
    var fullName by remember { mutableStateOf("") }
    var utaId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // States for validation
    var fullNameError by remember { mutableStateOf(false) }
    var utaIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    // State for profile image selection (simplified without using actual image)
    var isImageSelected by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Just mark that an image was selected, without trying to display it
        isImageSelected = uri != null
    }

    // Validation functions
    val validateFullName = { name: String ->
        name.isNotBlank()
    }

    val validateUtaId = { id: String ->
        id.length == 10 && id.isDigitsOnly()
    }

    val validateEmail = { email: String ->
        email.isNotBlank() && email.contains("@") && email.endsWith(".edu")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Header
            Text(
                text = "Create Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0795DD),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Profile Image Selector
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(if (isImageSelected) Color(0xFF0795DD) else Color(0xFFE6F3FA))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (isImageSelected) {
                    // Show placeholder for selected image
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
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
                            fullName = it
                            fullNameError = !validateFullName(it)
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
                        )
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

                    // UTA ID Field
                    OutlinedTextField(
                        value = utaId,
                        onValueChange = {
                            if (it.length <= 10 && it.isDigitsOnly()) {
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
                        )
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

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = it.isNotEmpty() && !validateEmail(it)
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
                        )
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

            // Register Button
            Button(
                onClick = {
                    // Validate all fields
                    fullNameError = !validateFullName(fullName)
                    utaIdError = !validateUtaId(utaId)
                    emailError = !validateEmail(email)

                    if (!fullNameError && !utaIdError && !emailError) {
                        // Validation successful - navigate to dashboard
                        Toast.makeText(context, "Profile created successfully", Toast.LENGTH_SHORT).show()

                        // Navigate to Dashboard
                        val intent = Intent(context, DashboardActivity::class.java)
                        context.startActivity(intent)
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
                    "Create Your Profile",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}