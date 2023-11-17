package com.playdeadrespawn.storyapp.data.injection

import android.content.Context
import com.playdeadrespawn.storyapp.data.RemoteData
import com.playdeadrespawn.storyapp.data.Repository
import com.playdeadrespawn.storyapp.data.api.ApiConfig
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val userPreferenceDatastore = UserPreference.getInstance(context.dataStore)
        val remoteDataSource = RemoteData.getInstance()
        return Repository.getInstance(apiService, userPreferenceDatastore, remoteDataSource)
    }
}