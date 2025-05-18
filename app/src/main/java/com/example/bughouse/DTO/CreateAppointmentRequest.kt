package com.example.bughouse.DTO

data class CreateAppointmentRequest(
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val appointmentDate: String,
    val startTime: String,
    val duration: Int = 60,
    val status: String = "scheduled"
)