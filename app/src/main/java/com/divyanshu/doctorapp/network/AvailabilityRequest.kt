package com.divyanshu.doctorapp.network

data class AvailabilityRequest(
    val doctor_id: Int,
    val date: String,
    val time_slot: String
)
