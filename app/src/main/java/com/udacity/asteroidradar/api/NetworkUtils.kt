package com.udacity.asteroidradar.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.constant.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NetworkUtils constructor(
    private val context: Context,
    private val connectivityManager: ConnectivityManager,
    private val networkRequest: NetworkRequest
) {
    companion object {
        @JvmStatic
        @Volatile
        var isConnected = false
        val isNetworkAvailable = MutableLiveData<Boolean>()
    }

    fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(networkRequest, getNetworkCallback())
    }

    private fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected = false
                isNetworkAvailable.postValue(false)
            }
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected = true
                isNetworkAvailable.postValue(isConnected)
            }
        }
    }

}

  fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<DatabaseAsteroid> {
        val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")
        val asteroidList = ArrayList<DatabaseAsteroid>()
        val asteroidDates = ArrayList<String>()

        for (key in nearEarthObjectsJson.keys()) {
            /** key for data-extraction */
            asteroidDates.add(key)
        }
        for (formattedDate in asteroidDates) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = DatabaseAsteroid(
                    id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
                )
                asteroidList.add(asteroid)
            }
        }
        return asteroidList
    }


/** sample Calendar utilization */
fun getNextSevenDaysFormattedDates(): ArrayList<String> {

    val formattedDateList = ArrayList<String>()
    formattedDateList.clear()
    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 6)
    }
    return formattedDateList
}