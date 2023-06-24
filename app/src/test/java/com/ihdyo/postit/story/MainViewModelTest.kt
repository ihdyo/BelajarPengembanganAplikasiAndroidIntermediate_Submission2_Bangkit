package com.ihdyo.postit.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ihdyo.postit.MainDispatcherRule
import com.ihdyo.postit.getOrAwaitValue
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.repository.UserRepository
import com.ihdyo.postit.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()
    @Rule
    @JvmField
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: UserRepository

    private lateinit var viewModel: MainViewModel

    private var token = "sampleToken"
    private var story = MutableLiveData<PagingData<DataDetail>>()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(storyRepository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `test stories 1`() = runTest(UnconfinedTestDispatcher()) {
        val dummyStory = TestStory.story()
        val data: PagingData<DataDetail> = Differ.snapshot(dummyStory)
        val expectedData = MutableLiveData<PagingData<DataDetail>>()
        expectedData.value = data

        `when`(storyRepository.getPagingStories(token)).thenReturn(story)

        val actual: PagingData<DataDetail> = story.getOrAwaitValue()
        val simlate = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        Assert.assertEquals(0, simlate.snapshot().size) // check size is null before submit data
        simlate.submitData(actual)
        Assert.assertEquals(dummyStory.size, simlate.snapshot().size) // checking for same size after submit data
        Assert.assertEquals(dummyStory[0], simlate.snapshot()[0]) // check the first data is same
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `test stories 2`() = runTest(UnconfinedTestDispatcher()) {
        val data: PagingData<DataDetail> = Differ.snapshot(emptyList())
        val expectedData = MutableLiveData<PagingData<DataDetail>>()
        expectedData.value = data

        `when`(storyRepository.getPagingStories(token)).thenReturn(story)

        val actual: PagingData<DataDetail> = story.getOrAwaitValue()
        val simlate = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        simlate.submitData(actual)
        Assert.assertEquals(0, simlate.snapshot().size) // Checking data is null
    }
}

private val updateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int){}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?){}
}


class Differ : PagingSource<Int, LiveData<List<DataDetail>>>() {
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