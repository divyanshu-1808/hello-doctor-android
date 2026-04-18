package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.AppointmentItem
import com.divyanshu.doctorapp.network.AppointmentRequest
import com.divyanshu.doctorapp.network.AppointmentStatusUpdate
import com.divyanshu.doctorapp.network.RetrofitInstance
import retrofit2.Response

class AppointmentRepository {

    suspend fun bookAppointment(request: AppointmentRequest): Response<AppointmentItem> {
        return RetrofitInstance.api.bookAppointment(request)
    }

    suspend fun getPatientAppointments(patientId: Int): Response<List<AppointmentItem>> {
        return RetrofitInstance.api.getPatientAppointments(patientId)
    }

    suspend fun getDoctorAppointments(doctorId: Int): Response<List<AppointmentItem>> {
        return RetrofitInstance.api.getDoctorAppointments(doctorId)
    }

    suspend fun getAllAppointments(): Response<List<AppointmentItem>> {
        return RetrofitInstance.api.getAllAppointments()
    }

    suspend fun cancelAppointment(appointmentId: Int): Response<AppointmentItem> {
        return RetrofitInstance.api.cancelAppointment(appointmentId)
    }

    suspend fun updateStatus(appointmentId: Int, status: String): Response<AppointmentItem> {
        return RetrofitInstance.api.updateAppointmentStatus(
            appointmentId,
            AppointmentStatusUpdate(status)
        )
    }
}