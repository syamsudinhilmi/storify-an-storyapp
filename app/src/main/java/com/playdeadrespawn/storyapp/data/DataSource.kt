package com.playdeadrespawn.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.playdeadrespawn.storyapp.data.response.AddNewStoryResponse
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.LoginResult
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataSource {
    fun getSession(): LiveData<LoginResult>
    fun login(email: String, password: String): LiveData<LoginResponse>
    fun register(name: String, email: String, password: String): LiveData<RegisterResponse>
    fun newStory(token: String, imageFile: File, desc: String, lon: String?, lat: String?): LiveData<AddNewStoryResponse>
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>>
    fun getMapsStory(token: String): LiveData<StoryResponse>
}