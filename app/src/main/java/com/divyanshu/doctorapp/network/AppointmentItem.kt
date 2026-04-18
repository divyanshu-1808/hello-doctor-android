package com.divyanshu.doctorapp.network

data class AppointmentItem(
    val id: Int,
    val patient_id: Int,
    val doctor_id: Int,
    val date: String,
    val time_slot: String,
    val status: String
)
