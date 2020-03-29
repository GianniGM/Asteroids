package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException
import timber.log.Timber

/**
 * Amazing worker that updates our database
 */
class UpdateAsteroidsWorker(
    private val context: Context, params: WorkerParameters
) : CoroutineWorker(appContext = context, params = params) {

    /*
     * Given I want to remove yesterdays asteroids instead of picking them one by one
     * I've just decided to wipe out the database and populate it with the fresh data
     * taken from apis.
     *
     * It of course possible to improve with some fails checks and only removing yesterdays data */
    override suspend fun doWork(): Result {
        Timber.d("WORK RUNNING!")

        val database = AsteroidsDatabase.getInstance(context).asteroidsDao
        val asteroidsRepository = AsteroidsRepository(database)

        return try {
            database.deleteAsteroids()
            asteroidsRepository.updateAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_ID = "UpdateAsteroidsWorker"
    }

}