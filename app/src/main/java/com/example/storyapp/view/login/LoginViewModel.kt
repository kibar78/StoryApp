package com.example.storyapp.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.result.ResultState
import com.example.storyapp.data.repository.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {

    fun saveSession(token: String){
        viewModelScope.launch {
            repository.saveSession(token)
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = repository.loginUser(email,password)
            saveSession("Bearer ${successResponse.loginResult.token}")
            emit(ResultState.Success(successResponse))
        }catch (e: Exception){
            Log.d("Repository", "Login: ${e.message.toString()} ")
            emit(ResultState.Error(e.message.toString()))
        }
    }
}