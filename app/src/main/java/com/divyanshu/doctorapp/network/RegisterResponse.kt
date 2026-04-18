package com.divyanshu.doctorapp.network

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)
