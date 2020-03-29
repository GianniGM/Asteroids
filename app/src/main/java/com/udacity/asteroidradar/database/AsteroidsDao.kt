package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Just a Room Dao interface
 */
@Dao
interface AsteroidsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroid: Asteroid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroid: List<Asteroid>)

    @Update
    fun update(asteroid: Asteroid)

    @Query("SELECT * FROM asteroid_list_table ORDER BY close_approach_date ASC")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_list_table ORDER BY close_approach_date ASC")
    fun getAllAsteroidsList(): List<Asteroid>

    @Query("DELETE FROM asteroid_list_table")
    fun deleteAsteroids()

    @Query("DELETE FROM asteroid_list_table WHERE id = :asteroidKey")
    fun deleteAsteroid(asteroidKey: Long)
}