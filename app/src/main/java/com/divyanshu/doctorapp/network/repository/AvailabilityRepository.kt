package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.Availability
import retrofit2.Response

class AvailabilityRepository {

    suspend fun getAvailability(doctorId: String): Response<List<Availability>> {
        return RetrofitInstance.api.getAvailability(doctorId)
    }
}