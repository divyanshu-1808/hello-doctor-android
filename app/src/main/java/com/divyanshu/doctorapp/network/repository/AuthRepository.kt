package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.auth.LoginRequest
import retrofit2.Response
import com.divyanshu.doctorapp.network.auth.LoginResponse

class AuthRepository {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return RetrofitInstance.api.login(
            LoginRequest(email, password)
        )
    }
}