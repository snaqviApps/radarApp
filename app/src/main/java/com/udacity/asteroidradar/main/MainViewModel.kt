package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.PictureOfDay
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getDatabaseInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


enum class RadarApiStatus { LOADING, ERROR, DONE }

class MainViewModel(val database: AsteroidDao, application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<RadarApiStatus>()
    val status: LiveData<RadarApiStatus>
        get() = _status

    private val _asteroidCallResponse = MutableLiveData<List<DatabaseAsteroid>>()
    /**
     *
     * doesn't work gives error:
     * * What went wrong:Execution failed for task ':app:kaptDebugKotlin'.
     * A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptExecution
     * java.lang.reflect.InvocationTargetException (no error message)
     */

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //  TODO_done (01): Create a viewModelJob and override onCancel() for cancelling coroutines
    private var viewModelJob = Job()

    //  TODO_done (02, 03): create a Asteroid liveData var and use a coroutine to initialize it from database
    private val _availableAsteroid = database.getLatestAsteroid()
    val availableDatabaseAsteroid: LiveData<DatabaseAsteroid>?
        get() = _availableAsteroid


    /** repository pattern */
    private val databaseInstance = getDatabaseInstance(application)
    private val asteroidRepository = AsteroidRepository(databaseInstance)

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
    }

    val asteroidList = asteroidRepository.repoCallResponse
    /** repository pattern ENDS HERE */

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