package com.playdeadrespawn.storyapp.view.storyadd

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.pref.UserModel
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.response.AddNewStoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryAddViewModel(private val repository: UserPreference) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun newStory(token: String, imageFile: File, description: String) {
        val description = description.toRequestBody("text/plain".toMediaType())
        val reqImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            reqImageFile
        )

        val client = ApiConfig.getApiService().newStory("Bearer $token", imagePart, description)
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(call: Call<AddNewStoryResponse>, response: Response<AddNewStoryResponse>) {
                when (response.code()) {
                    401 -> "${response.code()} :Bad Request"
                    403 -> "${response.code()} :Forbidden"
                    404 -> "${response.code()} :Not Found"
                    else -> "${response.code()}: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}
