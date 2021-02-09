package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(
    val database: AsteroidDao,
    application: Application
) : AndroidViewModel(application) {

    //  TODO_done (01): Create a viewModelJob and override onCancel() for cancelling coroutines
    private var viewModelJob = Job()

    //  TODO_done (02): create a Aseroid liveData var and use a coroutine to initialize it from database\
    private val _availableAsteroid = MutableLiveData<Asteroid>()
    val availableAsteroid: MutableLiveData<Asteroid>?
    get() = _availableAsteroid

    //  TODO_done (03): Define a scope of the coroutines to run in
    init {
        intializeaAvailableAsteroid()
    }

    private fun intializeaAvailableAsteroid() {
        viewModelScope.launch {
            _availableAsteroid.value = getAsteroidFromDatabase()
        }
    }

    private suspend fun getAsteroidFromDatabase(): LiveData<Asteroid>? {
        return database.getLatestAsteroid()
    }

    //  TODO_done (04): get all asteroids from database
    private val allAsteroids = database.getAllAsteroids()

    //  TODO_done (05): Add local functions for insert(), update() and clear()
    private suspend fun insert(asteroid: Asteroid) {
        viewModelScope.launch {
            database.insert(asteroid)
        }
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

}



