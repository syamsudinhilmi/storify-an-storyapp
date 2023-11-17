package com.playdeadrespawn.storyapp.view.storyadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.playdeadrespawn.storyapp.data.Repository
import com.playdeadrespawn.storyapp.data.response.LoginResult
import java.io.File

class StoryAddViewModel(private val repository: Repository) : ViewModel() {
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    fun getSession(): LiveData<LoginResult> {
        return repository.getSession()
    }

    fun newStory(token: String, imageFile: File, description: String, lon: String?, lat: String?) = repository.newStory(token, imageFile, description, lon, lat)
}
