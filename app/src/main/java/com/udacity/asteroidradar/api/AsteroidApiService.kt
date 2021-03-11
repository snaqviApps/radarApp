package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.END_POINT
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * This is complete URL to be constructed with to dynamic info
 * @param start_date
 * @param end_date
 *
 */

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .client(OkHttpClient().newBuilder().build())
    .build()

interface AsteroidApiService {
    @GET(END_POINT.plus("feed"))
    fun getAsteroids(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_Key") api_key: String
    )
            : Call<String>
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}