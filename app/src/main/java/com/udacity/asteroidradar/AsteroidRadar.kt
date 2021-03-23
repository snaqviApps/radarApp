package com.udacity.asteroidradar

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import com.udacity.asteroidradar.api.NetworkUtils

//Application class registered in Manifest file
class AsteroidRadar : Application() {
    lateinit var networkUtils: NetworkUtils

    override fun onCreate() {
        super.onCreate()
        networkUtils = NetworkUtils(this,
            getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager,
            NetworkRequest.Builder().build())
        networkUtils.registerNetworkCallback()
    }
}
