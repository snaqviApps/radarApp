package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroids: List<Asteroid>)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("SELECT * FROM asteroid_info_table WHERE asteroidId = :key")
    suspend fun get(key: Long): Asteroid?

    @Query("DELETE FROM asteroid_info_table")
    suspend fun clear()


    @Query("SELECT * FROM asteroid_info_table ORDER BY asteroidId DESC")
    fun getAllAsteroids(): LiveData<List<Asteroid>?>

    /**
     * Selects and returns the latest data.
     */
    @Query("SELECT * FROM ASTEROID_INFO_TABLE ORDER BY asteroidId DESC LIMIT 1")
    fun getLatestAsteroid(): LiveData<Asteroid>?

}