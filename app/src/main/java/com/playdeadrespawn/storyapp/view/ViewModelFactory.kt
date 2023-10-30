package com.playdeadrespawn.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.view.main.MainViewModel
import com.playdeadrespawn.storyapp.view.storyadd.StoryAddViewModel

class ViewModelFactory(
    private val repository: UserPreference,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoryAddViewModel::class.java) -> {
                StoryAddViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}