package com.example.bughouse.DTO

data class AttendanceLogData(
    val appointment_id: Int,
    val student_name: String,
    val course_code: String,
    val course_title: String,
    val appointment_date: String,
    val status: String,
    val checkin: String
)