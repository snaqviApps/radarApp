package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates

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

//    @Query("SELECT * FROM ASTEROID_INFO_TABLE where approach_date >= date (${getNextSevenDaysFormattedDates1()[0])} ORDER BY date(closeApproachDate) asc")
    @Query("SELECT * from asteroid_info_table where approach_date BETWEEN date(:approach_date) AND date(:endDate) ORDER BY date(approach_date) asc")
    fun getFilteredAsteroids(): LiveData<List<Asteroid>?>

    /**
     * Selects and returns the latest data.
     */
    @Query("SELECT * FROM ASTEROID_INFO_TABLE ORDER BY asteroidId DESC LIMIT 1")
    fun getLatestAsteroid(): LiveData<Asteroid>?

}