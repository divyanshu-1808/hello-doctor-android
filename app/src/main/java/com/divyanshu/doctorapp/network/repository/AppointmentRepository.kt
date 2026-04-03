package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.AppointmentRequest
import retrofit2.Response

class AppointmentRepository {

    suspend fun bookAppointment(request: AppointmentRequest): Response<Unit> {
        return RetrofitInstance.api.bookAppointment(request)
    }
}