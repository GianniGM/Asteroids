package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.NasaApiService
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repository for fetching data from data base or network
 */
class AsteroidsRepository(private val database: AsteroidsDao) {

    val asteroids = database.getAllAsteroids()

    suspend fun updateAsteroids() {
        val asteroids = getAsteroidsFromApi()
        withContext(Dispatchers.IO) {
            database.insertAll(asteroids)
        }
    }

    private suspend fun getAsteroidsFromApi(): List<Asteroid> {
        return try {
            NasaApiService.getAsteroids()
        } catch (t: Throwable) {
            Timber.d(t.localizedMessage ?: "Something bad and mysterious happened fetching data ")
            listOf()
        }
    }
}