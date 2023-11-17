package com.playdeadrespawn.storyapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.playdeadrespawn.storyapp.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getStory(token: String) = repository.getStory(token)
    fun getSession() = repository.getSession()
    fun getMapsStory(token: String) = repository.getMapsStory(token)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}