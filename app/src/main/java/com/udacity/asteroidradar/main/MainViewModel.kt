package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfTheDayApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level.INFO

class MainViewModel(val database: AsteroidDao,
                    application: Application) : AndroidViewModel(application) {

    private val _asteroidCallResponse = MutableLiveData<List<Asteroid>>()
    val asteroidCallResponse: MutableLiveData<List<Asteroid>>
        get() = _asteroidCallResponse

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //  TODO_done (01): Create a viewModelJob and override onCancel() for cancelling coroutines
    private var viewModelJob = Job()

    //  TODO_done (02): create a Aseroid liveData var and use a coroutine to initialize it from database
    private val _availableAsteroid = database.getLatestAsteroid()       // reads from database directly
    val availableAsteroid: LiveData<Asteroid>?
        get() = _availableAsteroid

    /**
     * TODO_done (02, 03): Define a scope of the coroutines
     */

    //  TODO_done (04): get all asteroids from database
    val asteroids = database.getAllAsteroids()

    //  TODO_done (05): Add local functions for insert(), update() and clear()
    private suspend fun insert(asteroids: List<Asteroid>) {
        viewModelScope.launch {
            database.insert(asteroids)
        }
    }

    init {
        viewModelScope.launch {
            getAsteroidsProperties()
        }

//        viewModelScope.launch {
//            getPictureOfDay()
//        }
    }

   /* private fun getPictureOfDay() {
        try {
            val pictureResult = PictureOfTheDayApi.picOfTheDayService.getPictureOfTheDay(BuildConfig.NASA_API_KEY)
            pictureResult.enqueue(object : Callback<PictureOfDay> {
                override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
                    Log.e("TAG", "exception: ${t.message}")
                }
                override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
                    _pictureOfDay.value = response.body()
                    Log.i("picture-json: ", _pictureOfDay.value.toString())
                }
            })
        } catch (e: Exception) {
            Log.i("picture-json: ", e.stackTraceToString())
        }
    }*/

    private fun getAsteroidsProperties() {
        AsteroidApi.retrofitService.getAsteroids(
                start_date = "2017-09-11",
                end_date = "2017-09-17",
                api_key = BuildConfig.NASA_API_KEY)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        _asteroidCallResponse.value = parseAsteroidsJsonResult(JSONObject(response.body()!!))
                        viewModelScope.launch {
                            insert(_asteroidCallResponse.value as ArrayList<Asteroid>)
                        }
                        print(("Response: " + _asteroidCallResponse.value))
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        print("nw-call-exception: ${t.message}")
                    }
                }).also {
                    try {
                        val pictureResult = PictureOfTheDayApi.picOfTheDayService.getPictureOfTheDay(BuildConfig.NASA_API_KEY)
                        pictureResult.enqueue(object : Callback<PictureOfDay> {
                            override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
                                Log.e("TAG", "exception: ${t.message}")
                            }

                            override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
                                _pictureOfDay.value = response.body()
                                Log.i("picture-json: ", _pictureOfDay.value.toString())
                            }
                        })
                    } catch (e: Exception) {
                        print("picture-json -exception: ${e.stackTrace}")
                        Log.i("picture-json: ", e.stackTraceToString())
                    }
                }
    }
    
    private suspend fun update(asteroid: Asteroid) {
        viewModelScope.launch {
            database.update(asteroid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        database.clear()
    }

    /** LiveDate (Observable) for Navigation */
    private val _navigateToDetailsFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailsFragment
        get() = _navigateToDetailsFragment

    fun onAsteroidClicked(asteroidSelected: Asteroid) {
        _navigateToDetailsFragment.value = asteroidSelected
    }

    fun onAsteroidNavigated() {
        _navigateToDetailsFragment.value = null
    }
}