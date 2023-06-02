package com.ihdyo.postit.di

import android.content.Context
import com.ihdyo.postit.api.APIConfig
import com.ihdyo.postit.database.StoryDatabase
import com.ihdyo.postit.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return MainRepository(database, apiService)
    }
}