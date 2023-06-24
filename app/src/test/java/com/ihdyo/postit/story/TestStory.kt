package com.ihdyo.postit.story

import com.ihdyo.postit.data.db.DataDetail

class TestStory {
    companion object{
        fun story(): List<DataDetail> {
            val story1 = DataDetail("0001","Hello", "Description", "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:924dcc7e3d877a61292899b4c4ca8f1820220124100739.jpeg", "2023-06-10T06:34:18.598Z", -4.466667, 135.199997)
            val story2 = DataDetail("0002","Hai", "Description", "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:924dcc7e3d877a61292899b4c4ca8f1820220124100739.jpeg", "2023-06-10T06:34:18.598Z", -4.466667, 135.199997)
            val story3 = DataDetail("0003","Aloha", "Description", "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:924dcc7e3d877a61292899b4c4ca8f1820220124100739.jpeg", "2023-06-10T06:34:18.598Z", -4.466667, 135.199997)
            val story4 = DataDetail("0004","Anyeong", "Description", "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:924dcc7e3d877a61292899b4c4ca8f1820220124100739.jpeg", "2023-06-10T06:34:18.598Z", -4.466667, 135.199997)
            return listOf(story1, story2, story3, story4)
        }
    }

}