package com.divyanshu.doctorapp.network

data class AppointmentRequest(
    val patient_id: Int,
    val doctor_id: Int,
    val date: String,
    val time_slot: String
)