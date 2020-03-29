package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Asteroid::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase: RoomDatabase(){

    abstract val asteroidsDao: AsteroidsDao

    companion object {
        const val DATABASE_NAME = "asteroids_database"

        @Volatile
        private var INSTANCE: AsteroidsDatabase? = null

        fun getInstance(context: Context) :AsteroidsDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidsDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }

                return instance
            }
        }

    }

}