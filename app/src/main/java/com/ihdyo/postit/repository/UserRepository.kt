package com.ihdyo.postit.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.ihdyo.postit.data.api.APIConfig
import com.ihdyo.postit.data.api.APIService
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.data.db.StoryDB
import com.ihdyo.postit.wrapEspressoIdlingResource
import com.ihdyo.postit.data.dc.LoginDataAccount
import com.ihdyo.postit.data.dc.RegisterDataAccount
import com.ihdyo.postit.data.dc.ResponseDetail
import com.ihdyo.postit.data.dc.ResponseLocationStory
import com.ihdyo.postit.data.dc.ResponseLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(
    private val storyDatabase: StoryDB,
    private val apiService: APIService
) {
    private val _stories = MutableLiveData<List<DataDetail>>()
    val stories: LiveData<List<DataDetail>> = _stories

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<ResponseLogin>()
    val userLogin: LiveData<ResponseLogin> = _userLogin

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            apiService.loginUser(loginDataAccount).enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        _userLogin.value = responseBody!!
                        _message.value = "Hello ${_userLogin.value!!.loginResult.name}!"
                    } else {
                        when (response.code()) {
                            401 -> _message.value = "Wrong data, try again!"
                            408 -> _message.value = "Lost connection, try again!"
                            else -> _message.value = "Error message: ${response.message()}"
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Error message: ${t.message}"
                }
            })
        }
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            apiService.registUser(registDataUser).enqueue(object : Callback<ResponseDetail> {
                override fun onResponse(
                    call: Call<ResponseDetail>,
                    response: Response<ResponseDetail>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _message.value = "Account created!"
                    } else {
                        when (response.code()) {
                            400 -> _message.value = "Email was taken, try another!"
                            408 -> _message.value = "Lost connection, try again!"
                            else -> _message.value = "Error message: ${response.message()}"
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Error message: ${t.message}"
                }
            })
        }
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        _isLoading.value = true
        apiService.addStory(photo, des, lat?.toFloat(), lng?.toFloat(), "Bearer $token")
            .enqueue(object : Callback<ResponseDetail> {
                override fun onResponse(
                    call: Call<ResponseDetail>,
                    response: Response<ResponseDetail>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.value = responseBody.message
                        }
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message
                }
            })
    }

    fun getStories(token: String) {
        _isLoading.value = true
        apiService.getLocationStory(32, 1, "Bearer $token").enqueue(object : Callback<ResponseLocationStory> {
            override fun onResponse(
                call: Call<ResponseLocationStory>,
                response: Response<ResponseLocationStory>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _stories.value = responseBody.listStory
                    }
                    _message.value = responseBody?.message.toString()
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseLocationStory>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message.toString()
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<DataDetail>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3
            ),
            remoteMediator = StoryRemote(storyDatabase, apiService, token),
            pagingSourceFactory = { storyDatabase.getListStoryDetailDao().getAllStories() }
        ).liveData
    }
}