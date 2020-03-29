package com.udacity.asteroidradar.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

//NOTE it's impossible to test iveData without mockito, mockito-android, and core-testing libraries
@RunWith(JUnit4::class)
class AsteroidsDatabaseTest {

    private lateinit var asteroidDao: AsteroidsDao
    private lateinit var db: AsteroidsDatabase

    @Before
    fun setUp() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AsteroidsDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        asteroidDao = db.asteroidsDao
    }

    @After
    @Throws(IOException::class)
    fun setDown() {
        db.close()
    }



    @Test
    fun insertAsteroid() {
        //Given
        val asteroid = givenTheAsteroids(1L, 2L, 3L)[0]

        //When
        asteroidDao.insert(asteroid)

        //Then
        val asteroids = asteroidDao.getAllAsteroidsList()
        assertEquals(asteroids.size, 1)
    }

    @Test
    fun insertAsteroids() {
        //Given
        val asteroid = givenTheAsteroids(1L, 2L, 3L)

        //When
        asteroidDao.insertAll(asteroid)

        //Then
        val asteroids = asteroidDao.getAllAsteroidsList()
        assertEquals(asteroids.size, 3)
    }

    @Test
    fun removeOneAsteroid(){
        //Given
        val asteroids = givenTheAsteroids(1L, 2L, 3L, 4L)
        asteroidDao.insertAll(asteroids)

        //When
        asteroidDao.deleteAsteroid(2L)

        //Then
        val actual = asteroidDao.getAllAsteroidsList()

        assertEquals(actual.size, 3)
        assert(!actual.map(Asteroid::id).contains(2L))
    }

    private fun givenTheAsteroids(vararg ids: Long): List<Asteroid> {
        val mutableList = mutableListOf<Asteroid>()
        for (id in ids) {
            mutableList.add(
                Asteroid(
                    id,
                    "asteroid",
                    "asteroid",
                    500.0,
                    500.0,
                    500.0,
                    500.0,
                    true
                )
            )
        }

        return mutableList
    }
}