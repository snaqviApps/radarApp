package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getDatabaseInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import timber.log.Timber


enum class RadarApiStatus { LOADING, ERROR, DONE }

class MainViewModel(val asteroidDao:  AsteroidDao, application: Application) :
    AndroidViewModel(application) {

    private val databaseInstance = getDatabaseInstance(application)
    private val asteroidRepository = AsteroidRepository(databaseInstance)

    init {
        Timber.i("init() called")
        mainViewModelRefreshAsteroidData()
    }

    fun mainViewModelRefreshAsteroidData() {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
    }

    val pictureOfDay = asteroidRepository.pictureOfDay
    val status = asteroidRepository.status
    val dbDataMainViewModel = asteroidRepository.downloadedData

    private suspend fun update(databaseAsteroid: DatabaseAsteroid) {
        viewModelScope.launch {
            asteroidDao.update(databaseAsteroid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        asteroidDao.clear()
    }

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