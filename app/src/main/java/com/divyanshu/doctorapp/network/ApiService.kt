package com.divyanshu.doctorapp.network

import com.divyanshu.doctorapp.network.auth.LoginRequest
import com.divyanshu.doctorapp.network.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("doctors")
    suspend fun getDoctors(): Response<List<Doctor>>

    @GET("availability/{doctor_id}")
    suspend fun getAvailability(
        @Path("doctor_id") doctorId: String
    ): Response<List<Availability>>

    @POST("appointments")
    suspend fun bookAppointment(
        @Body request: AppointmentRequest
    ): Response<Unit>
}