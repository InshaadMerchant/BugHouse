package com.example.bughouse.DTO

data class TutorAppointmentData(
    val id: Int,
    val tutorName: String,  // This actually contains the student name for tutors
    val courseName: String,
    val date: String,
    val time: String,
    val duration: Int,
    val status: String,
    val checkedIn: Boolean = false
)