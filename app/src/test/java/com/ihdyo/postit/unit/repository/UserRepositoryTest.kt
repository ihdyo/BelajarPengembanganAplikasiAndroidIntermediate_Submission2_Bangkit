package com.ihdyo.postit.unit.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.repository.UserRepository
import com.ihdyo.postit.utils.DataDummy.generateDummyNewStories
import com.ihdyo.postit.utils.DataDummy.generateDummyNewsEntity
import com.ihdyo.postit.utils.MainDispatcherRule
import com.ihdyo.postit.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File

class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainRepository: UserRepository

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        mainRepository = Mockito.mock(UserRepository::class.java)
    }

    @Test
    fun isGetStoriesWorking() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<DataDetail>>()
        expectedStories.value = dummyStories

        val token = "ini token"
        mainRepository.getStories(token)
        Mockito.verify(mainRepository).getStories(token)

        `when`(mainRepository.stories).thenReturn(expectedStories)

        val actualStories = mainRepository.stories.getOrAwaitValue()

        Mockito.verify(mainRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun isStoriesRightData() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<DataDetail>>()
        expectedStories.value = dummyStories

        `when`(mainRepository.stories).thenReturn(expectedStories)

        val actualStories = mainRepository.stories.getOrAwaitValue()

        Mockito.verify(mainRepository).stories

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun isMessageRightData() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainRepository.message.getOrAwaitValue()

        Mockito.verify(mainRepository).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun isPagingRightData() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        `when`(mainRepository.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mainRepository.isLoading.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoading
        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun isWorking() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "ini token"
        story.value = data

        `when`(mainRepository.getPagingStories(token)).thenReturn(story)

        val actualData = mainRepository.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(mainRepository).getPagingStories(token)
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