package com.ihdyo.postit.data.di

import android.content.Context
import com.ihdyo.postit.data.api.APIConfig
import com.ihdyo.postit.data.db.StoryDB
import com.ihdyo.postit.repository.UserRepository

object Inject {
    fun provideRepository(context: Context): UserRepository {
        val database = StoryDB.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return UserRepository(database, apiService)
    }
}