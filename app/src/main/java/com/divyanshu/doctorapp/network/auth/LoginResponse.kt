package com.divyanshu.doctorapp.network.auth

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user_id: Int,
    val role: String
)