package com.ihdyo.postit.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ihdyo.postit.data.db.DataDetail

@Dao
interface DataDetailDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<DataDetail>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, DataDetail>

    @Query("SELECT * FROM stories")
    fun getAllListStories(): List<DataDetail>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}