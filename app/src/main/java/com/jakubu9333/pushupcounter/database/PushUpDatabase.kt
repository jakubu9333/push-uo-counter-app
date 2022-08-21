package com.jakubu9333.pushupcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *
 * @author Jakub Uhlarik
 */

@Database(entities = [PushUps::class], version = 1, exportSchema = false)
abstract class PushUpDatabase: RoomDatabase() {



        abstract val pushUpsDao: PushUpsDao

        companion object {
            @Volatile
            private var INSTANCE: PushUpDatabase? = null

            fun getInstance(context: Context): PushUpDatabase {
                synchronized(this) {

                    var instance = INSTANCE
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext, PushUpDatabase::class.java,

                            "pushups_database"
                        )
                            .fallbackToDestructiveMigration()
                            .build()

                        INSTANCE = instance
                    }
                    return instance
                }

            }
        }


    }