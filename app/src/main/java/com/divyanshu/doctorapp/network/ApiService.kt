package com.divyanshu.doctorapp.network

import com.divyanshu.doctorapp.network.auth.LoginRequest
import com.divyanshu.doctorapp.network.auth.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ────────────────────────────────────────────────────────────────

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @Multipart
    @POST("register/doctor")
    suspend fun registerDoctor(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("specialization") specialization: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part licenseFile: MultipartBody.Part?
    ): Response<RegisterResponse>

    // ── Doctors ──────────────────────────────────────────────────────────────

    @GET("doctors")
    suspend fun getDoctors(
        @Query("specialization") specialization: String? = null
    ): Response<List<Doctor>>

    @GET("doctors/{doctor_id}")
    suspend fun getDoctorDetail(
        @Path("doctor_id") doctorId: Int
    ): Response<DoctorDetail>

    @DELETE("doctors/{doctor_id}")
    suspend fun deleteDoctor(@Path("doctor_id") doctorId: Int): Response<Unit>

    // ── User ─────────────────────────────────────────────────────────────────

    @GET("users/me")
    suspend fun getUserMe(): Response<UserResponse>

    // ── Admin ────────────────────────────────────────────────────────────────

    @GET("admin/pending-doctors")
    suspend fun getPendingDoctors(): Response<List<PendingDoctor>>

    @PUT("admin/approve/{doctor_id}")
    suspend fun approveDoctor(@Path("doctor_id") doctorId: Int): Response<Unit>

    @PUT("admin/reject/{doctor_id}")
    suspend fun rejectDoctor(@Path("doctor_id") doctorId: Int): Response<Unit>

    @GET("admin/doctors")
    suspend fun adminGetAllDoctors(): Response<List<DoctorDetail>>

    @GET("admin/patients")
    suspend fun adminGetPatients(): Response<List<PatientAdminInfo>>

    // ── Availability ─────────────────────────────────────────────────────────

    @GET("availability/{doctor_id}")
    suspend fun getAvailability(
        @Path("doctor_id") doctorId: String
    ): Response<List<Availability>>

    @POST("availability")
    suspend fun createAvailability(
        @Body request: AvailabilityRequest
    ): Response<Availability>

    @DELETE("availability/{availability_id}")
    suspend fun deleteAvailability(
        @Path("availability_id") availabilityId: Int
    ): Response<Unit>

    // ── Appointments ─────────────────────────────────────────────────────────

    @POST("appointments")
    suspend fun bookAppointment(@Body request: AppointmentRequest): Response<AppointmentItem>

    @GET("appointments/patient/{patient_id}")
    suspend fun getPatientAppointments(
        @Path("patient_id") patientId: Int
    ): Response<List<AppointmentItem>>

    @GET("appointments/doctor/{doctor_id}")
    suspend fun getDoctorAppointments(
        @Path("doctor_id") doctorId: Int
    ): Response<List<AppointmentItem>>

    @GET("appointments")
    suspend fun getAllAppointments(): Response<List<AppointmentItem>>

    @PUT("appointments/cancel/{appointment_id}")
    suspend fun cancelAppointment(
        @Path("appointment_id") appointmentId: Int
    ): Response<AppointmentItem>

    @PUT("appointments/{appointment_id}/status")
    suspend fun updateAppointmentStatus(
        @Path("appointment_id") appointmentId: Int,
        @Body update: AppointmentStatusUpdate
    ): Response<AppointmentItem>

    // ── AI ───────────────────────────────────────────────────────────────────

    @POST("recommend")
    suspend fun recommendSpecialization(
        @Body request: RecommendRequest
    ): Response<RecommendResponse>
}