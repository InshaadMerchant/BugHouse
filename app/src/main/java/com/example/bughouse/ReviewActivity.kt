package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme

class ReviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val tutorName = intent.getStringExtra("tutorName") ?: "Unknown Tutor"
        val courseName = intent.getStringExtra("courseName") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"

        // Log the appointment ID to verify it's being received correctly
        println("ReviewActivity received appointmentId: $appointmentId")

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReviewScreen(
                        appointmentId = appointmentId,
                        tutorName = tutorName,
                        courseName = courseName,
                        appointmentTime = appointmentTime,
                        appointmentDate = appointmentDate,
                        onBackPressed = { finish() },
                        onSubmitReview = { rating, feedback, tags ->
                            // Navigate to ReviewSummaryActivity with the review data
                            val intent = Intent(this, ReviewSummaryActivity::class.java).apply {
                                putExtra("appointmentId", appointmentId)
                                putExtra("tutorName", tutorName)
                                putExtra("courseName", courseName)
                                putExtra("appointmentTime", appointmentTime)
                                putExtra("appointmentDate", appointmentDate)
                                putExtra("rating", rating)
                                putExtra("reviewText", feedback)
                                putStringArrayListExtra("selectedTags", ArrayList(tags))
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewScreen(
    appointmentId: Int,
    tutorName: String,
    courseName: String,
    appointmentTime: String,
    appointmentDate: String,
    onBackPressed: () -> Unit,
    onSubmitReview: (Int, String, List<String>) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }

    // Define predefined feedback tags based on star rating
    val feedbackTags = when (rating) {
        1 -> listOf("Unprepared", "Late", "Unhelpful", "Poor Communication", "Didn't Know Material")
        2 -> listOf("Below Expectations", "Needs Improvement", "Disorganized", "Hard to Follow")
        3 -> listOf("Average", "OK", "Satisfactory", "Some Helpful Tips")
        4 -> listOf("Good", "Knowledgeable", "Helpful", "Clear Explanations")
        5 -> listOf("Excellent", "Very Knowledgeable", "Engaging", "Patient", "Highly Recommended")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rate Your Experience",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Rate Your Experience with the Tutor",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Tutor information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = tutorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = courseName,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$appointmentDate, $appointmentTime",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Star rating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How would you rate your session?",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star rating row
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { rating = i }
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                // Display rating text
                val ratingText = when (rating) {
                    1 -> "Poor"
                    2 -> "Fair"
                    3 -> "Good"
                    4 -> "Very Good"
                    5 -> "Excellent"
                    else -> "Tap to rate"
                }

                Text(
                    text = ratingText,
                    color = if (rating > 0) Color(0xFF0795DD) else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // Only show tags if rating is selected
            if (rating > 0) {
                Text(
                    text = "What did you think about this tutor?",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    feedbackTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF0795DD) else Color.LightGray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedTags = if (isSelected) {
                                        selectedTags - tag
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) Color(0xFF0795DD) else Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Review text area
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 16.dp),
                placeholder = { Text("Add additional comments here...") },
                label = { Text("Your Review") },
                shape = RoundedCornerShape(8.dp)
            )

            // Submit button
            Button(
                onClick = {
                    onSubmitReview(rating, reviewText, selectedTags)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0795DD),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = rating > 0 // Only enable if a rating is selected
            ) {
                Text(
                    text = "Submit Your Review",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}