package com.ihdyo.postit.utils

import com.ihdyo.postit.data.db.DataDetail

object Dummy {
    fun generateDummyNewsEntity(): List<DataDetail> {
        val storyList = ArrayList<DataDetail>()
        for (i in 0..5) {
            val stories = DataDetail(
                "id $i",
                "username",
                "description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:2979e516a20f11ebf05f0d735a5b288120220221083443.jpeg",
                "2023-06-23T22:22:22Z",
                null,
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }

    fun generateDummyNewStories(): List<DataDetail> {
        val storyList = ArrayList<DataDetail>()
        for (i in 0..5) {
            val stories = DataDetail(
                "id $i",
                "username",
                "description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:2979e516a20f11ebf05f0d735a5b288120220221083443.jpeg",
                "2023-06-23T22:22:22Z",
                null,
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }
}