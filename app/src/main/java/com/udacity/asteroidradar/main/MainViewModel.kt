package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.detail.PictureOfDay
import com.udacity.asteroidradar.api.NasaApiService
import com.udacity.asteroidradar.database.AsteroidsDao
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel for [MainFragment]
 */
class MainViewModel(
        database: AsteroidsDao,
        application: Application
) : AndroidViewModel(application), CoroutineScope {


    private val viewModelJob = Job()
    override val coroutineContext: CoroutineContext
        get() = viewModelJob + Dispatchers.Main

    private val _picOfTheDay = MutableLiveData<PictureOfDay>()
    val picOfTheDay: LiveData<PictureOfDay>
        get() = _picOfTheDay

    private val _navigateToDetails = MutableLiveData<Asteroid>()
    val navigateToDetails: LiveData<Asteroid>
        get() = _navigateToDetails

    private val asteroidsRepository = AsteroidsRepository(database)
    val asteroids = asteroidsRepository.asteroids

    init {
        fetchDailyPicture()
        refreshAsteroids()
    }

    private fun refreshAsteroids() = launch {
        asteroidsRepository.updateAsteroids()
    }

    private fun fetchDailyPicture() = launch {
        try {
            val picOfDay =NasaApiService.getDailyPicture()
            Timber.d("Pic of the day: ${picOfDay.url}")
            _picOfTheDay.value = picOfDay
        }catch (t: Throwable){
            Timber.w( t.localizedMessage ?: "Unknown Message")
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetails.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}