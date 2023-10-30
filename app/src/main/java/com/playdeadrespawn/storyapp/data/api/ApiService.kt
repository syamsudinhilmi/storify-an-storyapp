package com.playdeadrespawn.storyapp.data.api

import com.playdeadrespawn.storyapp.data.response.AddNewStoryResponse
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStory(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun newStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
    ): Call<AddNewStoryResponse>
}