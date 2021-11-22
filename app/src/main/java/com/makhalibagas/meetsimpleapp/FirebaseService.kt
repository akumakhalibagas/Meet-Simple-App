package com.makhalibagas.meetsimpleapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface FirebaseService {

    @POST("send")
    fun sendRemoteMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body remoteBody: String
    ): Call<String>

}