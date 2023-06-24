package com.ihdyo.postit.story

import com.ihdyo.postit.data.db.DataDetail

class TestStory {
    companion object{
        fun story(): List<DataDetail> {
            val story1 = DataDetail("story-id12345678","Raihan", "Test Desctipyion", "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png", "2023-05-10T06:34:18.598Z", -10.212, -16.002)
            val story2 = DataDetail("story-id41249291","Chaira", "Test Desctipyion", "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png", "2023-05-10T06:34:18.598Z", -10.212, -16.002)
            val story3 = DataDetail("story-id22149129","Raihan", "Test Desctipyion", "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png", "2023-05-10T06:34:18.598Z", -10.212, -16.002)
            val story4 = DataDetail("story-id04021232","Chaira", "Test Desctipyion", "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png", "2023-05-10T06:34:18.598Z", -10.212, -16.002)
            return listOf(story1, story2, story3, story4)
        }
    }

}