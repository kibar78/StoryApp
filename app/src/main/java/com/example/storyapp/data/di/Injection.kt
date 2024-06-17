package com.example.storyapp.data.di

import android.content.Context
import com.example.storyapp.data.local.datastore.UserPreferences
import com.example.storyapp.data.local.datastore.dataStore
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.repository.Repository


object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService,pref)
    }
}