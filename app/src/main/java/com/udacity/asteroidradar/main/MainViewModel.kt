package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val database: AsteroidDao,
                    application: Application) : AndroidViewModel(application) {

    private val _asteroidCallResponse =  MutableLiveData<List<Asteroid>>()
    val asteroidCallResponse: MutableLiveData<List<Asteroid>>
    get() = _asteroidCallResponse

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
    }
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
                    print("nw-call-exception: " + t.message)
                }
            })
    }

//  TODO_done (06): implement click handlers for Start, and Clear buttons using coroutines to do the database work
    private suspend fun update(asteroid: Asteroid){
        viewModelScope.launch {
            database.update(asteroid)
        }
    }

//  TODO_done (07): transform asteroids into a asteroid-String using 'formatAsteroids()'

    // TODO_Done: clear job as well
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clear()
        }
    }
    private suspend fun clear(){
        database.clear()
    }

    /** LiveDate (Observable) for Navigation */
    private val _navigateToDetailsFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailsFragment
    get() = _navigateToDetailsFragment

    fun onAsteroidClicked(asteroidSelected: Asteroid){
        _navigateToDetailsFragment.value = asteroidSelected
    }

    fun onAsteroidNavigated(){
        _navigateToDetailsFragment.value = null
    }
}




