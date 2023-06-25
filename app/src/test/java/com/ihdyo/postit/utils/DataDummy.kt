package com.ihdyo.postit.utils

import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.data.dc.LoginDataAccount
import com.ihdyo.postit.data.dc.LoginResult
import com.ihdyo.postit.data.dc.RegisterDataAccount
import com.ihdyo.postit.data.dc.ResponseLogin

object DataDummy {

    fun generateDummyNewsEntity(): List<DataDetail> {
        val newsList = ArrayList<DataDetail>()
        for (i in 0..5) {
            val stories = DataDetail(
                "Title $i",
                "Username",
                "Description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2023-06-25T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyNewStories(): List<DataDetail> {
        val newsList = ArrayList<DataDetail>()
        for (i in 0..5) {
            val stories = DataDetail(
                "Title $i",
                "Username",
                "Description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2023-06-25T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }
}