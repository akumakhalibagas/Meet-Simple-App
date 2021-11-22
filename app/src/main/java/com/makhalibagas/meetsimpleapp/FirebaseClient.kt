package com.makhalibagas.meetsimpleapp

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

inline fun <reified T> createWebService(baseUrl: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}