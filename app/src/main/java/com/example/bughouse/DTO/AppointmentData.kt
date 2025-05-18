package com.example.bughouse.DTO

data class AppointmentData(
    val id: Int,
    val tutorName: String,
    val courseName: String,
    val date: String,
    val time: String,
    val duration: Int,
    val status: String
)