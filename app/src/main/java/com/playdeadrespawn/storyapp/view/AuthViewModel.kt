package com.playdeadrespawn.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.playdeadrespawn.storyapp.data.Repository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel() {

    fun saveSession(name: String, userId: String, token: String) {
        viewModelScope.launch {
            repository.saveSession(name, userId, token)
        }
    }
    fun getSession() = repository.getSession()

    fun registerSession(name: String, email: String, password: String) = repository.register(name, email, password)

    fun loginSession(email: String, password: String) = repository.login(email, password)
}