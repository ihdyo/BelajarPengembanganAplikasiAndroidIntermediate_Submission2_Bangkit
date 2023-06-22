package com.ihdyo.postit.repository.utils

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
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
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
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }


    fun generateDummyRequestLogin(): LoginDataAccount {
        return LoginDataAccount("kopret1@gmail.com", "12345678")
    }

    fun generateDummyResponseLogin(): ResponseLogin {
        val newLoginResult = LoginResult("qwerty", "kevin", "ini-token")
        return ResponseLogin(false, "Login successfully", newLoginResult)
    }

    fun generateDummyRequestRegister(): RegisterDataAccount {
        return RegisterDataAccount("kevin", "123@gmail.com", "12345678")
    }

}