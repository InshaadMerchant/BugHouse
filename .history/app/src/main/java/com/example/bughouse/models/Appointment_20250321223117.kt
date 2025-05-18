package com.example.bughouse.models

data class Appointment(
    val id: String,
    val studentName: String,
    val courseCode: String,
    val date: String,
    val time: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
} 