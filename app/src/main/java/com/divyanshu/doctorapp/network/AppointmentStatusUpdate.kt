package com.divyanshu.doctorapp.network

data class AppointmentStatusUpdate(
    val status: String   // "booked", "completed", or "cancelled"
)
