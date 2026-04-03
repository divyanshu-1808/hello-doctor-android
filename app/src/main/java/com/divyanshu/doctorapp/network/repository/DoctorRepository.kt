package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.Doctor
import retrofit2.Response

class DoctorRepository {

    suspend fun getDoctors(): Response<List<Doctor>> {
        return RetrofitInstance.api.getDoctors()
    }
}