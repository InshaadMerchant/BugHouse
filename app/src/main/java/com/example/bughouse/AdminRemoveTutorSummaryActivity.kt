package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme

class AdminRemoveTutorSummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Extract data from intent
        val tutorName = intent.getStringExtra("TUTOR_NAME") ?: "Unknown Tutor"
        val reason = intent.getStringExtra("REASON") ?: "Unknown Reason"
        val reasonCategory = intent.getStringExtra("REASON_CATEGORY") ?: "Unknown"
        val removalId = intent.getStringExtra("REMOVAL_ID") ?: "Unknown ID"
        val removalDate = intent.getStringExtra("REMOVAL_DATE") ?: "Unknown Date"

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Changed to white background
                ) {
                    RemoveTutorSummaryScreen(
                        tutorName = tutorName,
                        reason = reason,
                        reasonCategory = reasonCategory,
                        removalId = removalId,
                        removalDate = removalDate
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveTutorSummaryScreen(
    tutorName: String,
    reason: String,
    reasonCategory: String,
    removalId: String,
    removalDate: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Submission Summary",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Tutor Removal Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0795DD)
                        )

                        Divider(color = Color.LightGray, thickness = 1.dp)

                        // Display tutor information
                        SummaryItem(label = "Tutor", value = tutorName)

                        // Display reason for removal - show the text box value if "Other" was selected
                        SummaryItem(
                            label = "Reason for Removal",
                            value = if (reasonCategory == "Other") reason else reasonCategory
                        )

                        // Display removal ID
                        SummaryItem(label = "Removal ID", value = removalId)

                        // Display removal date
                        SummaryItem(label = "Date", value = removalDate)
                    }
                }

                // Warning/Info Card
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
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Confirming this action will immediately remove the tutor from the system. This action cannot be undone.",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Button - now with increased height
                Button(
                    onClick = {
                        // Create intent and pass data to confirmation activity
                        val intent = Intent(
                            context,
                            AdminRemoveTutorConfirmationActivity::class.java
                        ).apply {
                            putExtra("TUTOR_NAME", tutorName)
                            putExtra("REASON", reason)
                            putExtra("REASON_CATEGORY", reasonCategory)
                            putExtra("REMOVAL_ID", removalId)
                            putExtra("REMOVAL_DATE", removalDate)
                        }
                        context.startActivity(intent)
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
                        "Confirm Removal",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                // Cancel Button - now with increased height
                Button(
                    onClick = {
                        // Simply finish the activity and return to the previous screen
                        (context as? ComponentActivity)?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp) // Increased height from 56.dp to 64.dp
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        "Cancel",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}