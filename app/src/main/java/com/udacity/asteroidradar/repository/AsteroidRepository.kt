package com.udacity.asteroidradar.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfDayApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.PictureOfDay
import com.udacity.asteroidradar.main.RadarApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Singleton

@Singleton
open class AsteroidRepository(private val database: AsteroidDatabase) {

    private val _status = MutableLiveData<RadarApiStatus>()
    val status: LiveData<RadarApiStatus>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    val latestAsteroid = database.asteroidDao.getLatestAsteroid()
    private val _downloadedData = database.asteroidDao.getAllAsteroids()
    val downloadedData: LiveData<List<DatabaseAsteroid>?>
        get() {
            return _downloadedData
        }

    suspend fun refreshAsteroids() {
        _status.value = RadarApiStatus.LOADING
        withContext(Dispatchers.IO) {
            try {
                val result = AsteroidApi.asteroidApiService.getAsteroids(
                    start_date = getNextSevenDaysFormattedDates()[0],
                    end_date = getNextSevenDaysFormattedDates()[1],
                    api_key = BuildConfig.NASA_API_KEY
                )
                database.asteroidDao.insert(parseAsteroidsJsonResult(JSONObject(result)))
                Timber.i("retrofit:Coroutines combo: $result")
                _status.value = RadarApiStatus.DONE
            } catch (exception: Exception) {
                Timber.e("error getting asteroid-data from server: ${exception.printStackTrace()}")
                exception.printStackTrace()
                _status.postValue(RadarApiStatus.ERROR)
            }
        }.also {
            try {
                val pictureResult = PictureOfDayApi.RETROFIT_PIC_OF_DAY_SERVICE.getPictureOfDay(
                    BuildConfig.NASA_API_KEY
                )
                _pictureOfDay.value = pictureResult
                _status.value = RadarApiStatus.DONE
                Timber.i(_pictureOfDay.value.toString())
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        Timber.e("no network error: ${e.message}")
                    }
                    is HttpException -> {
                        val httpErrorCode = e.code()
                        val errorMsg = e.message()
                        Timber.e("pic-of-day http-error: $httpErrorCode")
                    }
                    else -> {
                        Timber.e("Other error while trying to download picOfTheDay")
                    }
                }
                Timber.e("exception: ${e.message}")
                _status.value = RadarApiStatus.ERROR
                Timber.i("Exception picture of the day:  %s", e.stackTraceToString())
            }
        }
    }
}