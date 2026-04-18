package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RegisterRequest
import com.divyanshu.doctorapp.network.RegisterResponse
import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.auth.LoginRequest
import com.divyanshu.doctorapp.network.auth.LoginResponse
import retrofit2.Response

class AuthRepository {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return RetrofitInstance.api.login(
            LoginRequest(email, password)
        )
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ): Response<RegisterResponse> {
        return RetrofitInstance.api.register(
            RegisterRequest(name, email, password, role)
        )
    }
}