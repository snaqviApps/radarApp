package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.database.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

enum class RadarApiStatus { LOADING, ERROR, DONE }
class MainViewModel(
    val database: AsteroidDao,
    application: Application
) : AndroidViewModel(application) {

    private var dbBasedEndDate = ""
    private var dbBasedStartDate = ""
    private val _status = MutableLiveData<RadarApiStatus>()
    val status: LiveData<RadarApiStatus>
        get() = _status

    private val _asteroidCallResponse = MutableLiveData<List<DatabaseAsteroid>>()
    val databaseAsteroidCallResponse: MutableLiveData<List<DatabaseAsteroid>>
        get() = _asteroidCallResponse

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //  TODO_done (01): Create a viewModelJob and override onCancel() for cancelling coroutines
    private var viewModelJob = Job()

    //  TODO_done (02, 03): create a Asteroid liveData var and use a coroutine to initialize it from database
    private val _availableAsteroid = database.getLatestAsteroid()
    val availableDatabaseAsteroid: LiveData<DatabaseAsteroid>?
        get() = _availableAsteroid

    init {
        val testCharcters : TestJavaDataTypesUtils = TestJavaDataTypesUtils()
        println(testCharcters.inputCharactersAnalysis("How are you", charArrayOf('i', 'o')))

        getAsteroidsProperties()
        dbBasedStartDate = getNextSevenDaysFormattedDates()[0]
        dbBasedEndDate = getNextSevenDaysFormattedDates()[1]
    }

    /**
     * TODO_done (04):
     * get all asteroids from database,
     * feed to recyclerView
     */
    val asteroids = database.getFilteredAsteroids(dbBasedStartDate, dbBasedEndDate)
    private suspend fun insert(databaseAsteroids: List<DatabaseAsteroid>) {
        viewModelScope.launch {
            database.insert(databaseAsteroids)
        }
    }

    fun getAsteroidsProperties() {
        viewModelScope.launch {
            _status.value = RadarApiStatus.LOADING
            AsteroidApi.asteroidApiService.getAsteroids(
                start_date = getNextSevenDaysFormattedDates()[0],
                end_date = getNextSevenDaysFormattedDates()[1],
                api_key = BuildConfig.NASA_API_KEY
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    _asteroidCallResponse.value =
                        parseAsteroidsJsonResult(JSONObject(response.body()!!))
                    viewModelScope.launch {
                        insert(_asteroidCallResponse.value as ArrayList<DatabaseAsteroid>)
                    }
                    _status.value = RadarApiStatus.DONE
                    print(("Response: " + _asteroidCallResponse.value))
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _status.value = RadarApiStatus.ERROR
                    _asteroidCallResponse.value = ArrayList()
                    print("nw-call-exception: ${t.message}")
                }
            }).also {
                try {
                    _status.value = RadarApiStatus.LOADING
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
                    print("picture-json -exception: ${e.stackTrace}")
                    Log.i("picture-json: ", e.stackTraceToString())
                }
            }
        }

    }

    private suspend fun update(databaseAsteroid: DatabaseAsteroid) {
        viewModelScope.launch {
            database.update(databaseAsteroid)
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
    private val _navigateToDetailsFragment = MutableLiveData<DatabaseAsteroid?>()
    val navigateToDetailsFragment
        get() = _navigateToDetailsFragment

    fun onAsteroidClicked(databaseAsteroidSelected: DatabaseAsteroid) {
        _navigateToDetailsFragment.value = databaseAsteroidSelected
    }

    fun onAsteroidNavigated() {
        _navigateToDetailsFragment.value = null
    }
}