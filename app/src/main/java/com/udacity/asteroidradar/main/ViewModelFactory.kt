package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.database.AsteroidsDao
import java.lang.IllegalArgumentException

/**
 * Factory Implementation for View Models,
 * it injects database into MainViewModel
 */
class ViewModelFactory(
    private val database: AsteroidsDao,
    private val application: Application
): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown Model class")
    }
}