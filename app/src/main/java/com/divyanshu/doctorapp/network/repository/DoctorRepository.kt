package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.Doctor
import com.divyanshu.doctorapp.network.RetrofitInstance
import retrofit2.Response

class DoctorRepository {

    suspend fun getDoctors(specialization: String? = null): Response<List<Doctor>> {
        return RetrofitInstance.api.getDoctors(specialization)
    }
}