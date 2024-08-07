package com.example.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.result.ResultState
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.response.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File

class AddStoryViewModel(private val repository: Repository): ViewModel() {

    private val _uploadStory = MutableLiveData<ResultState<ErrorResponse>>()
    val uploadStory : LiveData<ResultState<ErrorResponse>> = _uploadStory

    val getSession: Flow<String?> = repository.getSession()

    fun uploadStory(imageFile: File, description: String, lat: Float?, lon: Float?){
        viewModelScope.launch {
            repository.getSession().collect{token->
                if (token != null){
                    _uploadStory.value = ResultState.Loading
                    val desc = description.toRequestBody("text/plain".toMediaType())
                    val lat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                    val lon = lon?.toString()?.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )
                    try {
                        val response = repository.uploadStory(token,multipartBody,desc,lat,lon)
                        _uploadStory.value = ResultState.Success(response)
                    }catch (e:Exception){
                        _uploadStory.value = ResultState.Error(e.message.toString())
                    }
                }
            }
        }
    }

}