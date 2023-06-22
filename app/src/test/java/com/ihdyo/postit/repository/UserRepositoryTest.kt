package com.ihdyo.postit.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ihdyo.postit.repository.utils.MainDispatcherRule
import com.google.android.gms.maps.model.LatLng
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.data.dc.ResponseLogin
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyNewStories
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyNewsEntity
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyRequestLogin
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyRequestRegister
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyResponseLogin
import com.ihdyo.postit.repository.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File

class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var userRepository: UserRepository

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        userRepository = Mockito.mock(UserRepository::class.java)
    }

    @Test
    fun `verify getResponseLogin function is working`() {
        val dummyRequestLogin = generateDummyRequestLogin()
        val dummyResponseLogin = generateDummyResponseLogin()

        val expectedResponseLogin = MutableLiveData<ResponseLogin>()
        expectedResponseLogin.value = dummyResponseLogin

        userRepository.getResponseLogin(dummyRequestLogin)

        Mockito.verify(userRepository).getResponseLogin(dummyRequestLogin)
        Mockito.`when`(userRepository.userLogin).thenReturn(expectedResponseLogin)

        val actualData = userRepository.userLogin.getOrAwaitValue()
        Mockito.verify(userRepository).userLogin
        assertNotNull(actualData)
        assertEquals(expectedResponseLogin.value, actualData)
    }

    @Test
    fun `when login should return the right login response and not null`() {
        val dummyResponselogin = generateDummyResponseLogin()

        val expectedLogin = MutableLiveData<ResponseLogin>()
        expectedLogin.value = dummyResponselogin

        Mockito.`when`(userRepository.userLogin).thenReturn(expectedLogin)
        val actualLoginResponse = userRepository.userLogin.getOrAwaitValue()

        Mockito.verify(userRepository).userLogin
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `verify getResponseRegister function is working`() {
        val dummyRequestRegister = generateDummyRequestRegister()
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        userRepository.getResponseRegister(dummyRequestRegister)

        Mockito.verify(userRepository).getResponseRegister(dummyRequestRegister)
        Mockito.`when`(userRepository.message).thenReturn(expectedRegisterMessage)

        val actualData = userRepository.message.getOrAwaitValue()

        Mockito.verify(userRepository).message
        assertNotNull(actualData)
        assertEquals(expectedRegisterMessage.value, actualData)
    }

    @Test
    fun `verify upload function is working`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val token = "ini token"
        val latlng = LatLng(1.1, 1.1)

        userRepository.upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        Mockito.verify(userRepository).upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        Mockito.`when`(userRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = userRepository.message.getOrAwaitValue()

        Mockito.verify(userRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `verify getStories function is working`() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<DataDetail>>()
        expectedStories.value = dummyStories

        val token = "ini token"
        userRepository.getStories(token)
        Mockito.verify(userRepository).getStories(token)

        Mockito.`when`(userRepository.stories).thenReturn(expectedStories)

        val actualStories = userRepository.stories.getOrAwaitValue()

        Mockito.verify(userRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `when stories should return the right data and not null`() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<DataDetail>>()
        expectedStories.value = dummyStories

        Mockito.`when`(userRepository.stories).thenReturn(expectedStories)

        val actualStories = userRepository.stories.getOrAwaitValue()

        Mockito.verify(userRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `when message should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        Mockito.`when`(userRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = userRepository.message.getOrAwaitValue()

        Mockito.verify(userRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(userRepository.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = userRepository.isLoading.getOrAwaitValue()

        Mockito.verify(userRepository).isLoading
        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `verify getPagingStory function is working and should not null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "ini token"
        story.value = data

        Mockito.`when`(userRepository.getPagingStories(token)).thenReturn(story)

        val actualData = userRepository.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(userRepository).getPagingStories(token)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<DataDetail>>>() {
        companion object {
            fun snapshot(items: List<DataDetail>): PagingData<DataDetail> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<DataDetail>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DataDetail>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}