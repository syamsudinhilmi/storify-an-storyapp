package com.playdeadrespawn.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.playdeadrespawn.storyapp.DataDummy
import com.playdeadrespawn.storyapp.StoryPagingSource
import com.playdeadrespawn.storyapp.data.Repository
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import com.playdeadrespawn.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storiesRepos: Repository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStory = DataDummy.generateDummyStoriesEntity()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU9QTWZtNmk4dWx3bWJYZGkiLCJpYXQiOjE3MDAyMjEyMzh9.wlXmF_H3-bK4lqg877vm6mcXCIBE4i6cS227FtZxl-U"
    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = TestCoroutineDispatcher(testCoroutineScheduler)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storiesRepos)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.getSnapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storiesRepos.getStory(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryItem> = mainViewModel.getStory(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyList = emptyList<ListStoryItem>()
        val expectedEmptyStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedEmptyStory.value = PagingData.from(emptyList)
        Mockito.`when`(storiesRepos.getStory(token)).thenReturn(expectedEmptyStory)

        val actualEmptyStory: PagingData<ListStoryItem> = mainViewModel.getStory(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualEmptyStory)
        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `When Get Story with Location`() {
        val expectStories = MutableLiveData<StoryResponse>()
        expectStories.postValue(dummyStoriesResponse)
        Mockito.`when`(storiesRepos.getMapsStory(token)).thenReturn(expectStories)
        val actualStories = mainViewModel.getMapsStory(token).getOrAwaitValue()
        Mockito.verify(storiesRepos).getMapsStory(token)
        assertNotNull(actualStories)
        assertEquals(expectStories.value, actualStories)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}