package com.playdeadrespawn.storyapp.view

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.pref.UserModel
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val repository: UserPreference) : ViewModel() {

    val login = MutableLiveData<LoginResponse>()
    val register = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()


    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun registerSession(name: String, email: String, password: String) {
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    register.postValue(true)
                } else {
                    register.postValue(false)
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                register.postValue(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun loginSession(email: String, password: String) {
        loading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    login.postValue(response.body())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
                loading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}