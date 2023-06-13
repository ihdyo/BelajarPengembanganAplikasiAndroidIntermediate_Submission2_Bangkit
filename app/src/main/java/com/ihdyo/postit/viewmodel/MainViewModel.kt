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
import com.ihdyo.postit.repository.MainRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val stories: LiveData<List<DataDetail>> = mainRepository.stories

    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = mainRepository.userlogin

    fun login(loginDataAccount: LoginDataAccount) {
        mainRepository.getResponseLogin(loginDataAccount)
    }

    fun register(registDataUser: RegisterDataAccount) {
        mainRepository.getResponseRegister(registDataUser)
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        mainRepository.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<DataDetail>> {
        return mainRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        mainRepository.getStories(token)
    }
}
