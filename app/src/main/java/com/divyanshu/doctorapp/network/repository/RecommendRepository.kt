package com.divyanshu.doctorapp.network.repository

import com.divyanshu.doctorapp.network.RecommendRequest
import com.divyanshu.doctorapp.network.RecommendResponse
import com.divyanshu.doctorapp.network.RetrofitInstance
import retrofit2.Response

class RecommendRepository {

    suspend fun recommend(symptoms: List<String>): Response<RecommendResponse> {
        return RetrofitInstance.api.recommendSpecialization(
            RecommendRequest(symptoms)
        )
    }
}
