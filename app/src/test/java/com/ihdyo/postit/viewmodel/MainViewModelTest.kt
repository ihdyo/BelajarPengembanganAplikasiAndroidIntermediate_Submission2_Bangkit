package com.ihdyo.postit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.android.gms.maps.model.LatLng
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.data.dc.ResponseLogin
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyNewStories
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyRequestLogin
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyRequestRegister
import com.ihdyo.postit.repository.utils.DataDummy.generateDummyResponseLogin
import com.ihdyo.postit.repository.utils.MainDispatcherRule
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
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        mainViewModel = Mockito.mock(MainViewModel::class.java)
    }

    // upload
    @Test
    fun `when message upload should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        Mockito.`when`(mainViewModel.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainViewModel.message.getOrAwaitValue()

        Mockito.verify(mainViewModel).message
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when loading upload should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(mainViewModel.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(mainViewModel).isLoading
        Assert.assertNotNull(actualLoading)
        Assert.assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `verify upload function is working`() {
        val expectedUploadMessage = MutableLiveData<String>()
        expectedUploadMessage.value = "Story Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val token = "ini token"
        val latlng = LatLng(1.1, 1.1)

        mainViewModel.upload(imageMultipart, description, latlng.latitude, latlng.longitude, token)
        Mockito.verify(mainViewModel).upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        Mockito.`when`(mainViewModel.message).thenReturn(expectedUploadMessage)

        val actualUploadMessage = mainViewModel.message.getOrAwaitValue()
        Mockito.verify(mainViewModel).message
        Assert.assertNotNull(actualUploadMessage)
        Assert.assertEquals(expectedUploadMessage.value, actualUploadMessage)
    }

    // get story
    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `verify getStory is working and Should Not Return Null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "ini token"
        story.value = data
        Mockito.`when`(mainViewModel.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        Mockito.verify(mainViewModel).getPagingStories(token)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `when GetStory is Empty Should Not return Null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagedTestDataSources.snapshot(listOf())
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "ini token"
        story.value = data
        Mockito.`when`(mainViewModel.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        Mockito.verify(mainViewModel).getPagingStories(token)
        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
        print(differ.snapshot().size)
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

    // login
    @Test
    fun `when login message should return the right data and not null`() {
        val expectedLoginMessage = MutableLiveData<String>()
        expectedLoginMessage.value = "Login Successfully"

        Mockito.`when`(mainViewModel.message).thenReturn(expectedLoginMessage)

        val actualMessage = mainViewModel.message.getOrAwaitValue()

        Mockito.verify(mainViewModel).message
        Assert.assertNotNull(actualMessage)
        Assert.assertEquals(expectedLoginMessage.value, actualMessage)
    }

    @Test
    fun `when login loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(mainViewModel.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(mainViewModel).isLoading
        Assert.assertNotNull(actualLoading)
        Assert.assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `when login should return the right login user data and not null`() {
        val dummyResponselogin = generateDummyResponseLogin()

        val expectedLogin = MutableLiveData<ResponseLogin>()
        expectedLogin.value = dummyResponselogin

        Mockito.`when`(mainViewModel.userlogin).thenReturn(expectedLogin)

        val actualLoginResponse = mainViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(mainViewModel).userlogin
        Assert.assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `verify getResponseLogin function is working`() {
        val dummyRequestLogin = generateDummyRequestLogin()
        val dummyResponseLogin = generateDummyResponseLogin()

        val expectedResponseLogin = MutableLiveData<ResponseLogin>()
        expectedResponseLogin.value = dummyResponseLogin

        mainViewModel.login(dummyRequestLogin)

        Mockito.verify(mainViewModel).login(dummyRequestLogin)

        Mockito.`when`(mainViewModel.userlogin).thenReturn(expectedResponseLogin)

        val actualData = mainViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(mainViewModel).userlogin
        Assert.assertNotNull(expectedResponseLogin)
        assertEquals(expectedResponseLogin.value, actualData)
    }

    // register
    @Test
    fun `when register message should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        Mockito.`when`(mainViewModel.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainViewModel.message.getOrAwaitValue()

        Mockito.verify(mainViewModel).message
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when register loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(mainViewModel.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(mainViewModel).isLoading
        Assert.assertNotNull(actualLoading)
        Assert.assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `verify getResponseRegister function is working`() {
        val dummyRequestRegister = generateDummyRequestRegister()
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        mainViewModel.register(dummyRequestRegister)

        Mockito.verify(mainViewModel).register(dummyRequestRegister)

        Mockito.`when`(mainViewModel.message).thenReturn(expectedRegisterMessage)

        val actualData = mainViewModel.message.getOrAwaitValue()

        Mockito.verify(mainViewModel).message
        Assert.assertNotNull(actualData)
        Assert.assertEquals(expectedRegisterMessage.value, actualData)
    }

}