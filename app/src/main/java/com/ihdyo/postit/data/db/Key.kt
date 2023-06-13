package com.ihdyo.postit.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class Key(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
