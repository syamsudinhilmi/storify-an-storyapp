@file:Suppress("DEPRECATION")

package com.playdeadrespawn.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.playdeadrespawn.storyapp.data.Repository
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import com.playdeadrespawn.storyapp.utils.DummyData
import com.playdeadrespawn.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    private lateinit var repository: Repository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStoriesResponse = DummyData.generateDummyStoriesResponse()
    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU9QTWZtNmk4dWx3bWJYZGkiLCJpYXQiOjE3MDAyMjEyMzh9.wlXmF_H3-bK4lqg877vm6mcXCIBE4i6cS227FtZxl-U"
    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = TestCoroutineDispatcher(testCoroutineScheduler)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mainViewModel = MainViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun `When Get Story with Location`() {
        val expectStories = MutableLiveData<StoryResponse>()
        expectStories.postValue(dummyStoriesResponse)
        Mockito.`when`(repository.getMapsStory(token)).thenReturn(expectStories)
        val actualStories = mainViewModel.getMapsStory(token).getOrAwaitValue()
        Mockito.verify(repository).getMapsStory(token)
        assertNotNull(actualStories)
        assertEquals(expectStories.value, actualStories)
    }
}

