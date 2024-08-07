package com.example.storyapp.view.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapp.data.result.ResultState
import com.example.storyapp.data.repository.Repository

class RegisterViewModel(private val repository: Repository): ViewModel() {

    fun registerUser(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = repository.registerUser(name, email, password)
            emit(ResultState.Success(successResponse))
        }catch (e: Exception){
            Log.d("Repository", "Register: ${e.message.toString()} ")
            emit(ResultState.Error(e.message.toString()))
        }
    }
}