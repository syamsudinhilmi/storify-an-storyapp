package com.playdeadrespawn.storyapp.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playdeadrespawn.storyapp.data.UserRepository
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import com.playdeadrespawn.storyapp.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(userRepository: UserRepository) : ViewModel() {
    val register = MutableLiveData<Boolean>()
    val login = MutableLiveData<Boolean>()

    fun registerSession(name: String, email: String, password: String) {
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    register.postValue(true)
                } else {
                    register.postValue(false)
                    Log.e("AuthViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                register.postValue(false)
                Log.e("AuthViewModel", "onFailure: ${t.message}")
            }
        })
    }

    fun loginSession(email: String, password: String) {
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    login.postValue(true)
                } else {
                    login.postValue(false)
                    Log.e("AuthViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                login.postValue(false) // Login failed
                Log.e("AuthViewModel", "onFailure: ${t.message}")
            }
        })
    }
}