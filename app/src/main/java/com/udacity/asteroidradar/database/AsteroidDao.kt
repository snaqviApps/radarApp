package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseAsteroids: List<DatabaseAsteroid>)

    @Update
    suspend fun update(databaseAsteroid: DatabaseAsteroid)

    @Query("SELECT * FROM asteroid_info_table WHERE asteroidId = :key")
    suspend fun get(key: Long): DatabaseAsteroid?

    @Query("DELETE FROM asteroid_info_table")
    suspend fun clear()

    @Query("SELECT * FROM asteroid_info_table ORDER BY asteroidId DESC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>?>

    @Query("SELECT * from asteroid_info_table where approach_date BETWEEN date(:startDate) AND date(:endDate) ORDER BY date(approach_date) asc")
    fun getFilteredAsteroids(startDate: String, endDate: String): LiveData<List<DatabaseAsteroid>?>

    /**
     * Selects and returns the latest data.
     */
    @Query("SELECT * FROM ASTEROID_INFO_TABLE ORDER BY asteroidId DESC LIMIT 1")
    fun getLatestAsteroid(): LiveData<DatabaseAsteroid>?

}