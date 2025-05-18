package com.example.bughouse.DTO

data class ProfileData(
    val userId: String,
    val fullName: String,
    val email: String,
    val userType: String,
    val studentIdNumber: String?
)

data class ProfileUpdateRequest(
    val fullName: String,
    val email: String,
    val studentIdNumber: String?
)