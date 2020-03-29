package com.udacity.asteroidradar.api
import android.os.Build
import androidx.annotation.RequiresApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.detail.PictureOfDay
import com.udacity.asteroidradar.utils.CalendarUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Network service, it cares about fetching and parsing data fetched from the Apis
 */
object NasaApiService {

    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private val moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(getClient())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(NasaApi::class.java)

    /**
     * Return a suspend function that calls the daily picture
     *
     * @return the picture of the day [PictureOfDay]
     */
    suspend fun getDailyPicture() = retrofit.getPictureOfTheDay(apiKey = Constants.API_KEY)

    /**
     * Get all the asteroids between today and the following 7 days,
     * then it parse them into a list of [Asteroid]
     *
     * @return the list of asteroids
     */
    suspend fun getAsteroids(): List<Asteroid> {
        val jsonObject = getAsteroids(getDate()).toJSONObject()
        return parseAsteroidsJsonResult(jsonObject).toList()
    }

    private suspend fun getAsteroids(date: Pair<String, String>): String {
        val (startDate, endDate) = date
        return retrofit.getAsteroidFeed(
            startDate = startDate,
            endDate = endDate,
            apiKey = Constants.API_KEY
        )
    }

    private fun getDate(): Pair<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val startDate: LocalDateTime = LocalDateTime.now()
            val endDate: LocalDateTime = startDate.plusDays(Constants.DEFAULT_END_DATE_DAYS.toLong())
            format(startDate) to format(endDate)
        } else {
            //remove this and all its dependencies after we increase our minimum version
            val calendar = CalendarUtils.getCalendarInstance()
            val startDate = calendar.today
            val endDate = calendar.setEndDate(Constants.DEFAULT_END_DATE_DAYS).endDate
            startDate to endDate
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun format(date: LocalDateTime): String {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
        return date.format(formatter)
    }

    private fun String.toJSONObject(): JSONObject {
        return JSONObject(this)
    }
}