package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfTheDayApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.PictureOfDay
import com.udacity.asteroidradar.main.RadarApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class RadarApiStatus { LOADING, ERROR, DONE }

//open class AsteroidRepository (private val asteroidDAO:AsteroidDao) {
open class AsteroidRepository (private val database: AsteroidDatabase) {

    private val _status = MutableLiveData<RadarApiStatus>()
    val status: LiveData<RadarApiStatus>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay


    private val _repoCallResponse = MutableLiveData<List<DatabaseAsteroid>>()
    val repoCallResponse : LiveData<List<DatabaseAsteroid>>              // encapsulation does not work, error: kapt
        get() {
            return _repoCallResponse
        }

    open suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidList = AsteroidApi.asteroidApiService.getAsteroids(
                start_date = getNextSevenDaysFormattedDates()[0],
                end_date = getNextSevenDaysFormattedDates()[1],
                api_key = BuildConfig.NASA_API_KEY
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    _repoCallResponse.value =
                        parseAsteroidsJsonResult(JSONObject(response.body()))
//                    _status.value = RadarApiStatus.DONE
                    print(("Response: " + _repoCallResponse.value))
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _status.value = RadarApiStatus.DONE
                }
            }.also {
                try {
//                    _status.value = RadarApiStatus.LOADING
                    val pictureResult =
                        PictureOfTheDayApi.retrofitPicOfTheDayService.getPictureOfTheDay(
                            BuildConfig.NASA_API_KEY
                        )
                    pictureResult.enqueue(object : Callback<PictureOfDay> {
                        override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
//                            _status.value = RadarApiStatus.ERROR
                            Log.e("TAG", "exception: ${t.message}")
                        }
                        override fun onResponse(
                            call: Call<PictureOfDay>,
                            response: Response<PictureOfDay>
                        ) {
                            _pictureOfDay.value = response.body()
//                            _status.value = RadarApiStatus.DONE
                            Log.i("picture-json: ", _pictureOfDay.value.toString())
                        }
                    })
                } catch (e: Exception) {
                    print("picture-json -exception: ${e.stackTrace}")
                    Log.i("picture-json: ", e.stackTraceToString())
                }

//            }).wait()
            //            Error: E/AndroidRuntime: FATAL EXCEPTION: main
//    Process: com.udacity.asteroidradar, PID: 23502
//    java.lang.IllegalMonitorStateException: object not locked by thread before wait()
            })

//            database.asteroidDao.insert(asteroidList as List<DatabaseAsteroid>)
//            database.asteroidDao.insert(asteroidList as LiveData<List<DatabaseAsteroid>>)
            database.asteroidDao.insert(asteroidList as List<DatabaseAsteroid>)             // warning on 'as': 'this cast can never succeed'
        }
    }
}


