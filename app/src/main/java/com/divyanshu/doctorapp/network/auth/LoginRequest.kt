package com.divyanshu.doctorapp.network.auth

data class LoginRequest(
    val email: String,
    val password: String
)