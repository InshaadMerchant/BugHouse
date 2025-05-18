package com.example.bughouse

import ApiClient
import android.content.Intent
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
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
import com.example.bughouse.DTO.ReviewSubmissionRequest
import com.example.bughouse.ui.theme.BugHouseTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewSummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val tutorName = intent.getStringExtra("tutorName") ?: "Unknown Tutor"
        val courseName = intent.getStringExtra("courseName") ?: "Unknown Course"
        val appointmentTime = intent.getStringExtra("appointmentTime") ?: "Unknown Time"
        val appointmentDate = intent.getStringExtra("appointmentDate") ?: "Unknown Date"
        val rating = intent.getIntExtra("rating", 0)
        val reviewText = intent.getStringExtra("reviewText") ?: ""
        val selectedTags = intent.getStringArrayListExtra("selectedTags")?.toList() ?: emptyList()

        // Log the appointment ID to verify it's being received correctly
        Log.d("ReviewSummary", "Received appointmentId: $appointmentId")

        setContent {
            BugHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReviewSummaryScreen(
                        appointmentId = appointmentId,
                        tutorName = tutorName,
                        courseName = courseName,
                        appointmentTime = appointmentTime,
                        appointmentDate = appointmentDate,
                        rating = rating,
                        reviewText = reviewText,
                        selectedTags = selectedTags,
                        onBackPressed = { finish() },
                        onConfirmSubmission = { isSuccess ->
                            if (isSuccess) {
                                // Navigate to ReviewSubmissionConfirmationActivity
                                val intent = Intent(this, ReviewSubmissionConfirmationActivity::class.java).apply {
                                    putExtra("tutorName", tutorName)
                                    putExtra("courseName", courseName)
                                    putExtra("appointmentTime", appointmentTime)
                                    putExtra("appointmentDate", appointmentDate)
                                    putExtra("rating", rating)
                                }
                                startActivity(intent)
                                finish() // Close this activity
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewSummaryScreen(
    appointmentId: Int,
    tutorName: String,
    courseName: String,
    appointmentTime: String,
    appointmentDate: String,
    rating: Int,
    reviewText: String,
    selectedTags: List<String>,
    onBackPressed: () -> Unit,
    onConfirmSubmission: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Set initial error state if appointment ID is invalid
    if (appointmentId == -1) {
        error = "Invalid appointment ID"
    }

    // Function to submit the review
    fun submitReview() {
        if (appointmentId == -1) {
            error = "Invalid appointment ID"
            return
        }

        isSubmitting = true
        error = null

        val reviewRequest = ReviewSubmissionRequest(
            appointmentId = appointmentId,
            rating = rating,
            reviewText = reviewText,
            tags = selectedTags
        )

        // Log the request details
        Log.d("ReviewSubmission", "Submitting review for appointmentId: $appointmentId")
        Log.d("ReviewSubmission", "Rating: $rating")
        Log.d("ReviewSubmission", "Tags: $selectedTags")

        ApiClient.apiService.submitReview(reviewRequest).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                isSubmitting = false
                if (response.isSuccessful) {
                    Log.d("ReviewSubmission", "Review submitted successfully")
                    Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                    onConfirmSubmission(true)
                } else {
                    val errorMsg = "Failed to submit review: ${response.message()}"
                    Log.e("ReviewSubmission", errorMsg)
                    error = errorMsg
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    onConfirmSubmission(false)
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                isSubmitting = false
                val errorMsg = "Network error: ${t.message}"
                Log.e("ReviewSubmission", errorMsg, t)
                error = errorMsg
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                onConfirmSubmission(false)
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Review Summary",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Your Review Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Error message if any
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = error ?: "",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color(0xFFB71C1C)
                    )
                }
            }

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tutor information section
                    Text(
                        text = "Tutor Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0795DD)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
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

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray
                    )

                    // Rating section
                    Text(
                        text = "Your Rating",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0795DD)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Star display
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarOutline,
                                    contentDescription = null,
                                    tint = if (index < rating) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Rating text
                        val ratingText = when (rating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            5 -> "Excellent"
                            else -> "Not Rated"
                        }

                        Text(
                            text = ratingText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }

                    // Tags section if any are selected
                    if (selectedTags.isNotEmpty()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )

                        Text(
                            text = "Selected Tags",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0795DD)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedTags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFE6F3FA),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tag,
                                        color = Color(0xFF0795DD),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Review comments section if provided
                    if (reviewText.isNotEmpty()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )

                        Text(
                            text = "Your Comments",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0795DD)
                        )

                        Text(
                            text = reviewText,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Confirm submission button
            Button(
                onClick = { submitReview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0795DD),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSubmitting && appointmentId != -1
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Confirm Your Submission",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}