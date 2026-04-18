package com.divyanshu.doctorapp.network

data class PendingDoctor(
    val id: Int,
    val name: String,
    val specialization: String,
    val bio: String?,
    val license_path: String?,
    val is_approved: Boolean,
    val user_id: Int?
)
