package com.example.bughouse.DTO

data class TutorData(
    val tutor_id: Int,
    val full_name: String,
    val rating: Double,
    val department: String
)

data class ScheduleData(
    val schedule_id: Int,
    val day_of_week: Int,
    val start_time: String,
    val end_time: String,
    val is_available: Boolean
)