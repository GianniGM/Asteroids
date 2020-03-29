package com.udacity.asteroidradar.utils

import android.util.Log
import com.udacity.asteroidradar.Constants
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Util class for java [Calendar]
 *
 * Calendar in Java is a struggle, so I created a wrapper that returns only today date and the expected date
 * I wanted to make them public in order to be reusable, but I guess is not the topic for this project*/
class CalendarUtils private constructor(private val pattern: String, private var locale: Locale) {
    private lateinit var calendar: Calendar
    private val instance = false

    val today: String = today().format()
    var endDate: String = today().format()

    fun setEndDate(days: Int): CalendarUtils {
        endDate = plusDays(days).format()
        return this
    }

    private fun format(): String {
        val formatter = SimpleDateFormat(
            pattern,
            locale
        )
        val currentTime = calendar.time
        return formatter.format(currentTime)
    }

    private fun today(): CalendarUtils {
        calendar = Calendar.getInstance()
        return this
    }

    private fun plusDays(days: Int): CalendarUtils {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return this
    }

    companion object {

        /**
         * I want to instanciate this only once */
        private var INSTANCE: CalendarUtils? = null
        fun getCalendarInstance(locale: Locale = Locale.getDefault()): CalendarUtils {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = CalendarUtils(Constants.API_QUERY_DATE_FORMAT, locale)
                    INSTANCE = instance
                } else {
                    instance.locale = locale
                }
                return instance
            }
        }
    }

}

/**
 * nice utils functions it returns true if the string
 */
infix fun String.dateIsBetween(date: Pair<String, String>): Boolean {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )
    return try {
        val currentTime = formatter.parse(this)
        val startTime = formatter.parse(date.first)
        val finalEndTime = formatter.parse(date.second)

        currentTime?.let {
            if(it == startTime || it == finalEndTime){
                true
            } else {
                currentTime.before(finalEndTime) && currentTime.after(startTime)
            }
        }?: false

    } catch (e: ParseException) {
        Timber.w( e.localizedMessage?: "Wrong parsing")
        false
    }
}
