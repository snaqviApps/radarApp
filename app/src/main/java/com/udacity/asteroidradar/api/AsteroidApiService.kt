package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.END_POINT
import com.udacity.asteroidradar.Constants.PictureOfTHEDAY_END_POINT
import com.udacity.asteroidradar.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
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

private val moshiPicOfTheDay = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
private val retrofitPicOfTheDay = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshiPicOfTheDay))
        .baseUrl(BASE_URL)
        .build()

interface AsteroidApiService {
    @GET(END_POINT.plus("feed"))
    fun getAsteroids(
            @Query("start_date") start_date: String,
            @Query("end_date") end_date: String,
            @Query("api_key") api_key: String
    ): Call<String>
}

interface PictureOfTheDayApiService {
    @GET(PictureOfTHEDAY_END_POINT)
    fun getPictureOfTheDay(
            @Query("api_key") api_key: String):
            Call<PictureOfDay>
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}

object PictureOfTheDayApi {
    val picOfTheDayService: PictureOfTheDayApiService by lazy {
        retrofitPicOfTheDay.create(PictureOfTheDayApiService::class.java)
    }
}