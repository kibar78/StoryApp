package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.StoriesRemoteMediator
import com.example.storyapp.data.local.datastore.UserPreferences
import com.example.storyapp.data.local.room.StoriesDatabase
import com.example.storyapp.data.local.room.StoryEntity
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
    private val storiesDatabase: StoriesDatabase
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

    suspend fun getDetailStories(token: String, id: String): DetailStoriesResponse{
        return apiService.getDetailStories(token,id)
    }

    fun getStories(token: String): LiveData<PagingData<StoryEntity>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase,apiService,token),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().getAllStory()
            }
        ).liveData
    }

    suspend fun uploadStory(token: String, imageFile: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): ErrorResponse{
        return apiService.uploadStory(token,imageFile,description,lat,lon)
    }

    suspend fun getLocation(token: String, location: Int): StoriesResponse{
        return apiService.getStoriesWithLocation(token, location)
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            pref: UserPreferences,
            storiesDatabase: StoriesDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref, storiesDatabase )
            }.also { instance = it }
    }
}