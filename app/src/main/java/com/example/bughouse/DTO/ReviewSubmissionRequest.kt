package com.example.bughouse.DTO

data class ReviewSubmissionRequest(
    val appointmentId: Int,
    val rating: Int,
    val reviewText: String,
    val tags: List<String>  // Tag names, not IDs
)