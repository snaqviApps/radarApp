package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getDatabaseInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, param: WorkerParameters) :
    CoroutineWorker(appContext, param) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabaseInstance(applicationContext)
        val repository = AsteroidRepository(database)
        WorkManager.getInstance()
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Timber.e("error in running minute-updates: %s", e.message())
            Result.retry()
        }
    }

}