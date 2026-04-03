package com.divyanshu.doctorapp.network

import com.divyanshu.doctorapp.network.auth.LoginRequest
import com.divyanshu.doctorapp.network.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import com.divyanshu.doctorapp.network.Doctor

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("doctors")
    suspend fun getDoctors(): Response<List<Doctor>>

}