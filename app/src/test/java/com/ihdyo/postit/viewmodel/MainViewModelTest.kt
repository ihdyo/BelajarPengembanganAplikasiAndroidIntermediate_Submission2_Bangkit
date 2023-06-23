package com.ihdyo.postit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.android.gms.maps.model.LatLng
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.repository.UserRepository
import com.ihdyo.postit.utils.Dummy.generateDummyNewStories
import com.ihdyo.postit.utils.DispatcherRule
import com.ihdyo.postit.utils.getOrAwaitValue
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
class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var dispatcherRule = DispatcherRule()

    private lateinit var userRepository: UserRepository

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        userRepository = Mockito.mock(UserRepository::class.java)
    }

    @Test
    suspend fun isMessageUploadedReturnRightDataAndNotNull() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Uploaded"

        Mockito.`when`(userRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = userRepository.message.getOrAwaitValue()

        Mockito.verify(userRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    suspend fun isLoadingUploadReturnRightDataAndNotNull() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(userRepository.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = userRepository.isLoading.getOrAwaitValue()

        Mockito.verify(userRepository).isLoading
        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    suspend fun isUploadFunctionWorking() {
        val expectedUploadMessage = MutableLiveData<String>()
        expectedUploadMessage.value = "Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "description".toRequestBody("text/plain".toMediaType())
        val token = "sampleToken"
        val latlng = LatLng(1.1, 1.1)

        userRepository.upload(imageMultipart, description, latlng.latitude, latlng.longitude, token)
        Mockito.verify(userRepository).upload(
            imageMultipart,
            description,
            latlng.latitude,
            latlng.longitude,
            token
        )

        Mockito.`when`(userRepository.message).thenReturn(expectedUploadMessage)

        val actualUploadMessage = userRepository.message.getOrAwaitValue()
        Mockito.verify(userRepository).message
        assertNotNull(actualUploadMessage)
        assertEquals(expectedUploadMessage.value, actualUploadMessage)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun isGetStoriesWorkingAndNotNull() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagingData.from(dummyStory)
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "sampleToken"
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
        assertEquals(dummyStory[0].name, differ.snapshot().items.getOrNull(0)?.name)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun isGetStoriesEmptyAndNotReturnNull() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagingData.empty<DataDetail>()
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "sampleToken"
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
        assertTrue(differ.snapshot().isEmpty())
        print(differ.snapshot().size)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }
}