package com.divyanshu.doctorapp.network

data class Availability(
    val id: Int,
    val doctor_id: Int,
    val date: String,
    val time_slot: String
)