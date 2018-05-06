package com.sampleprojects.poststest.model

import android.arch.persistence.room.*
import android.content.Context

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDAO(): PostDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "poststest.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}