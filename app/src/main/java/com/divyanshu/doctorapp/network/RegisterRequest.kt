package com.divyanshu.doctorapp.network

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)
