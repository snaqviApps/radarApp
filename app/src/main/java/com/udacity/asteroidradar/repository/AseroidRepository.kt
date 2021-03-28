package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class AsteroidRepository (private val database: AsteroidDatabase) {

//  ----- future implementation
//    suspend fun refreshAsteroids(){
//        withContext(Dispatchers.IO){
//            val asteroidList = AsteroidApi.asteroidApiService.getAsteroids().await()
//        }
//    }
}