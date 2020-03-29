package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.detail.PictureOfDay
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for fetching data from nasa api
 */
interface NasaApi {

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ):String

    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(@Query("api_key") apiKey: String): PictureOfDay

}
