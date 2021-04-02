package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.constant.Constants.BASE_URL
import com.udacity.asteroidradar.constant.Constants.END_POINT
import com.udacity.asteroidradar.constant.Constants.PictureOfDAY_END_POINT
import com.udacity.asteroidradar.database.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


/**
 * This is complete URL to be constructed with to dynamic info
 * @param start_date
 * @param end_date
 *
 */
private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(OkHttpClient().newBuilder()
                /**
                 * To utilize in case of connection - sync - time-outs
                    .connectTimeout(50, TimeUnit.MILLISECONDS)
                    .readTimeout(50, TimeUnit.MILLISECONDS)
                    .callTimeout(10, TimeUnit.MILLISECONDS)
                */
                .build())
        .build()

private val moshiPicOfDay = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
private val retrofitPicOfTheDay = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshiPicOfDay))
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

interface PictureOfDayApiService {
    @GET(PictureOfDAY_END_POINT)
    fun getPictureOfDay(
            @Query("api_key") api_key: String):
            Call<PictureOfDay>
}

object AsteroidApi {
    val asteroidApiService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}

object PictureOfDayApi {
    val RETROFIT_PIC_OF_DAY_SERVICE: PictureOfDayApiService by lazy {
        retrofitPicOfTheDay.create(PictureOfDayApiService::class.java)
    }
}