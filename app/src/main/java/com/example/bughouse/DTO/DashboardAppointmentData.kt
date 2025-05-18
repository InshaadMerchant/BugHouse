package com.example.bughouse.DTO

data class DashboardAppointmentData(
    val id: Int,
    val tutorName: String,
    val courseName: String,
    val date: String,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val time: String,
    val duration: Int,
    val status: String,
    // This is true when checkin = "Yes" in the database
    val checkedIn: Boolean
)