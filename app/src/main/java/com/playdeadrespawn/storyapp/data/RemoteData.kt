package com.playdeadrespawn.storyapp.data

import androidx.lifecycle.MutableLiveData
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.response.AddNewStoryResponse
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteData {
    val error = MutableLiveData("")
    var responsecode = ""

    fun login(callback: LoginCallback, email: String, password: String) {
        callback.onLogin(
            LoginResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if(response.isSuccessful){
                    response.body()?.let { callback.onLogin(it) }
                }else {
                    when (response.code()) {
                        200 -> responsecode = "200"
                        400 -> responsecode = "400"
                        401 -> responsecode = "401"
                        else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                    }
                    callback.onLogin(
                        LoginResponse(
                            null,
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onLogin(
                    LoginResponse(
                        null,
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun register(callback: RegisterCallback, name: String, email: String, password: String){
        val signupinfo = RegisterResponse(
            true,
            ""
        )
        callback.onRegister(
            signupinfo
        )
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onRegister(it) }
                    responsecode = "201"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }else {
                    responsecode = "400"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback.onRegister(
                    RegisterResponse(
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun newStory(callback: NewStoryCallback, token: String, imageFile: File, desc: String, lon: String? = null, lat: String? = null){
        callback.onNewStory(
            addStoryResponse = AddNewStoryResponse(
                true,
                ""
            )
        )

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val logitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/*".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().newStory(bearer = "Bearer $token", imageMultipart, description, latitude, logitude)

        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.error){
                        callback.onNewStory(responseBody)
                    }else{
                        callback.onNewStory(
                            addStoryResponse = AddNewStoryResponse(
                                true,
                                "Gagal upload file"
                            )
                        )
                    }
                }
                else{
                    callback.onNewStory(
                        addStoryResponse = AddNewStoryResponse(
                            true,
                            "Gagal upload file"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                callback.onNewStory(
                    addStoryResponse = AddNewStoryResponse(
                        true,
                        "Gagal upload file"
                    )
                )
            }
        })
    }

    fun getMapsStory(callback: GetMapsStoryCallback, token: String){
        val client = ApiConfig.getApiService().getMapStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { callback.onMapsStoryLoad(it) }
                }else{
                    val storyResponse = StoryResponse(
                        emptyList(),
                        true,
                        "Load Failed!"
                    )
                    callback.onMapsStoryLoad(storyResponse)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                val storyResponse = StoryResponse(
                    emptyList(),
                    true,
                    t.message.toString()
                )
                callback.onMapsStoryLoad(storyResponse)
            }
        })
    }


    interface LoginCallback{
        fun onLogin(signinResponse: LoginResponse)
    }
    interface RegisterCallback{
        fun onRegister(signupResponse: RegisterResponse)
    }
    interface GetMapsStoryCallback{
        fun onMapsStoryLoad(storyResponse: StoryResponse)
    }
    interface NewStoryCallback{
        fun onNewStory(addStoryResponse: AddNewStoryResponse)
    }
    companion object {
        @Volatile
        private var instance: RemoteData? = null

        fun getInstance(): RemoteData =
            instance ?: synchronized(this) {
                instance ?: RemoteData()
            }
    }
}