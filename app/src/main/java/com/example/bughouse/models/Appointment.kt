package com.example.bughouse.models

data class Appointment(
    val id: String,
    val studentName: String,
    val courseCode: String,
    val date: String,
    val time: String,
    val duration: String,
    val status: AppointmentStatus
)
