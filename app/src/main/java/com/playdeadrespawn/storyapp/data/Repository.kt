package com.playdeadrespawn.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.playdeadrespawn.storyapp.data.api.ApiService
import com.playdeadrespawn.storyapp.data.database.StoryDatabase
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.response.AddNewStoryResponse
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.LoginResponse
import com.playdeadrespawn.storyapp.data.response.LoginResult
import com.playdeadrespawn.storyapp.data.response.RegisterResponse
import com.playdeadrespawn.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

class Repository(
    private val apiService: ApiService,
    private val preference: UserPreference,
    private val remoteData: RemoteData,
    private val storyDatabase: StoryDatabase
) : DataSource {

    override  fun getSession(): LiveData<LoginResult> {
        return preference.getSession().asLiveData()
    }
    suspend fun saveSession(name: String, userId: String, token: String) {
        preference.saveSession(name, userId, token)
    }
    suspend fun logout() {
        preference.logout()
    }

    override fun login(email: String, password: String): LiveData<LoginResponse> {
        val inResponse = MutableLiveData<LoginResponse>()
        remoteData.login(object : RemoteData.LoginCallback{
            override fun onLogin(loginResponse: LoginResponse) {
                inResponse.postValue(loginResponse)
            }
        } ,email, password)
        return inResponse
    }

    override fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<RegisterResponse> {
        val regisResponse = MutableLiveData<RegisterResponse>()

        remoteData.register(object : RemoteData.RegisterCallback{
            override fun onRegister(registerResponse: RegisterResponse) {
                regisResponse.postValue(registerResponse)
            }
        }, name, email, password)
        return regisResponse
    }

    override fun newStory(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<AddNewStoryResponse> {
        val upResponse = MutableLiveData<AddNewStoryResponse>()

        remoteData.newStory(object : RemoteData.NewStoryCallback{
            override fun onNewStory(addNewStoryResponse: AddNewStoryResponse) {
                upResponse.postValue(addNewStoryResponse)
            }
        }, token, imageFile, desc, lon, lat)
        return upResponse
    }

    override fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPager(
                    api = apiService,
                    dsUserPref = preference
                )
            }
        ).liveData
    }

    override fun getMapsStory(token: String): LiveData<StoryResponse> {
        val mapResponse = MutableLiveData<StoryResponse>()

        remoteData.getMapsStory(object : RemoteData.GetMapsStoryCallback{
            override fun onMapsStoryLoad(storyResponse: StoryResponse) {
                mapResponse.postValue(storyResponse)
            }
        }, token)
        return mapResponse
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreference,
            remoteDataSource: RemoteData,
            storyDatabase: StoryDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref, remoteDataSource, storyDatabase)
            }.also { instance = it }
    }
}