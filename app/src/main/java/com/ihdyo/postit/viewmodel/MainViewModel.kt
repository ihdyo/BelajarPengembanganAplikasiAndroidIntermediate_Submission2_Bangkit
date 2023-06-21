package com.ihdyo.postit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.data.dc.LoginDataAccount
import com.ihdyo.postit.data.dc.RegisterDataAccount
import com.ihdyo.postit.data.dc.ResponseLogin
import com.ihdyo.postit.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    val stories: LiveData<List<DataDetail>> = userRepository.stories

    val message: LiveData<String> = userRepository.message

    val isLoading: LiveData<Boolean> = userRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = userRepository.userLogin

    fun login(loginDataAccount: LoginDataAccount) {
        userRepository.getResponseLogin(loginDataAccount)
    }

    fun register(registDataUser: RegisterDataAccount) {
        userRepository.getResponseRegister(registDataUser)
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        userRepository.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<DataDetail>> {
        return userRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        userRepository.getStories(token)
    }
}
