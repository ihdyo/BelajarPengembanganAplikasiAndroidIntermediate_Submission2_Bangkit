package com.ihdyo.postit.data.di

import android.content.Context
import com.ihdyo.postit.data.api.APIConfig
import com.ihdyo.postit.data.db.StoryDB
import com.ihdyo.postit.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDB.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return MainRepository(database, apiService)
    }
}