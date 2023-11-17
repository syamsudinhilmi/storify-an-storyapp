package com.playdeadrespawn.storyapp.view.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.playdeadrespawn.storyapp.data.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {
    fun getSession() = repository.getSession()
    fun getMapsStory(token: String) = repository.getMapsStory(token)
}