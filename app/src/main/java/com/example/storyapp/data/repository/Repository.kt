package com.example.storyapp.data.repository

import com.example.storyapp.data.local.datastore.UserPreferences
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.response.DetailStoriesResponse
import com.example.storyapp.data.response.ErrorResponse
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.response.StoriesResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
){

    suspend fun registerUser(name: String, email: String, password: String) : RegisterResponse{
        return apiService.register(name,email,password)
    }

    suspend fun loginUser(email: String, password: String) : LoginResponse {
        return apiService.login(email,password)
    }

    suspend fun saveSession(token: String){
        userPreferences.saveSession(token)
    }

    fun getSession(): Flow<String?> = userPreferences.getSession()

    suspend fun logout() = userPreferences.logout()

    suspend fun getAllStories(token: String): StoriesResponse {
        return apiService.getStories(token)
    }

    suspend fun getDetailStories(token: String, id: String): DetailStoriesResponse{
        return apiService.getDetailStories(token,id)
    }

    suspend fun uploadStory(token: String, imageFile: MultipartBody.Part, description: RequestBody): ErrorResponse{
        return apiService.uploadStory(token,imageFile,description)
    }
    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            pref: UserPreferences,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref)
            }.also { instance = it }
    }
}