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


open class AsteroidRepository(private val database: AsteroidDatabase) {

    private val _status = MutableLiveData<RadarApiStatus>()
    val status: LiveData<RadarApiStatus>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _repoCallResponse = MutableLiveData<List<DatabaseAsteroid>>()
    val repoCallResponse: LiveData<List<DatabaseAsteroid>>
        get() {
            return _repoCallResponse
        }

    suspend fun refreshAsteroids() {
        _status.value = RadarApiStatus.LOADING
        withContext(Dispatchers.IO) {
            AsteroidApi.asteroidApiService.getAsteroids(
                start_date = getNextSevenDaysFormattedDates()[0],
                end_date = getNextSevenDaysFormattedDates()[1],
                api_key = BuildConfig.NASA_API_KEY
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    _repoCallResponse.value =
                        parseAsteroidsJsonResult(JSONObject(response.body()))
                    _status.value = RadarApiStatus.DONE
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _status.value = RadarApiStatus.ERROR
                }
            }.also {
                try {
                    val pictureResult =
                        PictureOfTheDayApi.retrofitPicOfTheDayService.getPictureOfTheDay(
                            BuildConfig.NASA_API_KEY
                        )
                    pictureResult.enqueue(object : Callback<PictureOfDay> {
                        override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
                            _status.value = RadarApiStatus.ERROR
                            Log.e("TAG", "exception: ${t.message}")
                        }

                        override fun onResponse(
                            call: Call<PictureOfDay>,
                            response: Response<PictureOfDay>
                        ) {
                            _pictureOfDay.value = response.body()
                            _status.value = RadarApiStatus.DONE
                            Log.i("picture-json: ", _pictureOfDay.value.toString())
                        }
                    })
                } catch (e: Exception) {
                    Log.i("picture-json: ", e.stackTraceToString())
                }
            })
            _repoCallResponse.value?.let { database.asteroidDao.insert(it) }
        }
    }
}