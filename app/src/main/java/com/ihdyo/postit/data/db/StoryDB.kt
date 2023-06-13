package com.ihdyo.postit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataDetail::class, Key::class], version = 2, exportSchema = false)
abstract class StoryDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: StoryDB? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDB::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    abstract fun getListStoryDetailDao(): DataDetailDAO
    abstract fun getRemoteKeysDao(): KeyDAO
}
