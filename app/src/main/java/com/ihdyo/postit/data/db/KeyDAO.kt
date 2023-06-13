package com.ihdyo.postit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ihdyo.postit.data.db.Key

@Dao
interface KeyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<Key>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): Key?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}