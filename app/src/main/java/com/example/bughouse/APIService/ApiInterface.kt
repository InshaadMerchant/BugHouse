package com.example.bughouse.APIService
import com.example.bughouse.DTO.*
import retrofit2.Call
import retrofit2.http.*;
import retrofit2.http.Body
public interface ApiInterface {
    @GET("/upcoming-appointments/{userId}")
    fun getUpcomingAppointments(@Path("userId") userId: String): Call<List<AppointmentData>>
    @GET("/courses")
    fun getCourses(): Call<List<CourseData>>
    @POST("/create-appointment")
    fun createAppointment(@Body appointmentRequest: CreateAppointmentRequest): Call<Map<String, String>>
    @POST("/submit-review")
    fun submitReview(@Body reviewSubmissionRequest: ReviewSubmissionRequest): Call<Map<String, String>>
    @PUT("/cancel-appointment/{appointmentId}")
    fun cancelAppointment(@Path("appointmentId") appointmentId: Int): Call<Map<String, String>>
    @GET("/user-appointments/{userId}")
    fun getUserAppointments(@Path("userId") userId: String): Call<List<DashboardAppointmentData>>
    @GET("/tutors-by-course/{courseId}")
    fun getTutorsByCourse(@Path("courseId") courseId: Int): Call<List<TutorData>>

    @GET("/tutor-schedules/{tutorId}")
    fun getTutorSchedules(@Path("tutorId") tutorId: Int): Call<List<ScheduleData>>
    @PUT("/checkin-appointment/{appointmentId}")
    fun checkinAppointment(@Path("appointmentId") appointmentId: Int): Call<Map<String, String>>
    @POST("/getresponse")
    fun postObj(@Body obj: GraphMSALResponse ): Call<UserInfo> ;
    @GET("/profile/{userId}")
    fun getProfile(@Path("userId") userId: String): Call<ProfileData>
    @GET("/appointments/{userId}")
    fun getAppointments(@Path("userId") userId: String): Call<List<AppointmentData>>
    @PUT("/profile/{userId}")
    fun updateProfile(
        @Path("userId") userId: String,
        @Body profileUpdateRequest: ProfileUpdateRequest
    ): Call<Map<String, String>>
    @GET("/tutor-profile/{userId}")
    fun getTutorProfile(@Path("userId") userId: String): Call<TutorProfileData>

    @PUT("/tutor-profile/{userId}")
    fun updateTutorProfile(
        @Path("userId") userId: String,
        @Body tutorProfileUpdateRequest: TutorProfileUpdateRequest
    ): Call<Map<String, String>>
    // Add these methods to your existing ApiInterface.kt file
    // Add this to your ApiInterface.kt
    @GET("/tutor-attendance-logs/{userId}")
    fun getTutorAttendanceLogs(@Path("userId") userId: String): Call<List<TutorAttendanceLogData>>
    @GET("/tutor-appointments/{userId}")
    fun getTutorAppointments(@Path("userId") userId: String): Call<List<TutorAppointmentData>>

    @GET("/tutor-upcoming-appointments/{userId}")
    fun getTutorUpcomingAppointments(@Path("userId") userId: String): Call<List<TutorAppointmentData>>
}