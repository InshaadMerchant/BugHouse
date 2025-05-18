package com.example.bughouse.DTO

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class TutorAttendanceLogData(
    val appointment_id: Int,
    val student_name: String,
    val course_code: String,
    val course_title: String,
    val appointment_date: String,
    val status: String,
    val checkin: String,
    // Computed property for easier use in UI
    val isCheckedIn: Boolean = checkin == "Yes",
    // Add these properties with default values
    val dayOfMonth: Int = extractDayOfMonth(appointment_date),
    val month: Int = extractMonth(appointment_date),
    val year: Int = extractYear(appointment_date),
    val time: String = "00:00", // Default time
    val courseName: String = "$course_code - $course_title"
) {
    companion object {
        // Extract date components from appointment_date string
        private fun extractDayOfMonth(dateString: String): Int {
            try {
                // Assuming format like "April 25, 2025"
                val parts = dateString.split(" ")
                return parts[1].replace(",", "").toInt()
            } catch (e: Exception) {
                return 1 // Default day if parsing fails
            }
        }

        private fun extractMonth(dateString: String): Int {
            try {
                // Convert month name to number (0-based index)
                val monthMap = mapOf(
                    "January" to 0, "February" to 1, "March" to 2, "April" to 3,
                    "May" to 4, "June" to 5, "July" to 6, "August" to 7,
                    "September" to 8, "October" to 9, "November" to 10, "December" to 11
                )
                val monthName = dateString.split(" ")[0]
                return monthMap[monthName] ?: 0
            } catch (e: Exception) {
                return 0 // Default month (January) if parsing fails
            }
        }

        private fun extractYear(dateString: String): Int {
            try {
                // Assuming format ends with year
                return dateString.split(" ").last().toInt()
            } catch (e: Exception) {
                return 2025 // Default year if parsing fails
            }
        }
    }
}