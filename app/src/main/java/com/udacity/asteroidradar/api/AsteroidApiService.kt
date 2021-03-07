package com.udacity.asteroidradar.api


import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.END_POINT
import com.udacity.asteroidradar.database.Asteroid
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * This is complete URL to be constructed with to dynamic info
 * @param start_date
 * @param end_date
 *
 * https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=eLv0eWDrNCHEnQiVwZzI2BzyheaUjhP05ghpTS9v
 *
 */

private const val SAMPLE_UDACITY_BASE_URL = "https://mars.udacity.com/realestate"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient().newBuilder().build())
    .build()

interface AsteroidApiService {
    @GET(END_POINT.plus("feed"))
    fun getAsteroids(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_Key") api_key: String
    )
            : Call<List<Asteroid>>
}

/**
 * GET-Endpoint, exposed for consumption
 */

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}


