package com.example.bughouse.DTO

data class TutorProfileData(
    val userId: String,
    val fullName: String,
    val email: String,
    val userType: String,
    val employeeId: String?,
    val netId: String?,
    val phoneNumber: String?,
    val department: String?,
    val numCoursesTeaching: Int?,
    val isActive: Boolean?
)

data class TutorProfileUpdateRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val employeeId: String?,
    val netId: String?,
    val department: String?,
    val numCoursesTeaching: Int?
)