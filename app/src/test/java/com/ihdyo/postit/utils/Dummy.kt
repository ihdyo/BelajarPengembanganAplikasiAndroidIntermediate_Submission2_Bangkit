package com.ihdyo.postit.utils

import com.ihdyo.postit.data.db.DataDetail

object Dummy {
    fun generateDummyNewStories(): List<DataDetail> {
        val newsList = ArrayList<DataDetail>()
        for (i in 0..5) {
            val stories = DataDetail(
                "id $i",
                "username",
                "description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:2979e516a20f11ebf05f0d735a5b288120220221083443.jpeg",
                "2023-07-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }
}