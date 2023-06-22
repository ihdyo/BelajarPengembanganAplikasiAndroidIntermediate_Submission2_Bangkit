package com.ihdyo.postit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.utils.Dummy.generateDummyNewStories
import com.ihdyo.postit.utils.DispatcherRule
import com.ihdyo.postit.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var dispatcherRule = DispatcherRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    private lateinit var mockFile: File

    @Before
    fun setUp() {
        mainViewModel = Mockito.mock(MainViewModel::class.java)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun ifStoryNotNull() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagingData.from(dummyStory)
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "sampleToken"
        story.value = data
        `when`(mainViewModel.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        assertEquals(1, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot().items[0]?.name)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun ifStoryNull() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagingData.empty<DataDetail>()
        val story = MutableLiveData<PagingData<DataDetail>>()
        val token = "sampleToken"
        story.value = data
        `when`(mainViewModel.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        assertTrue(differ.snapshot().isEmpty())
        println(differ.snapshot().size)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }
}