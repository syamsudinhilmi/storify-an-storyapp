package com.playdeadrespawn.storyapp.view.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.pref.UserModel
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserPreference) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory(token: String){
        val client = ApiConfig.getApiService().getStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful){
                    _listStory.value = response.body()?.listStory
                    Log.e(TAG, "isSuccess: ${response.body()?.message}")
                } else {
                    Log.e(TAG, "onFailure: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
}