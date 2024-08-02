package com.example.storyapp.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel(){

    val getSession: Flow<String?> = repository.getSession()

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(token: String) = repository.getStories(token).cachedIn(viewModelScope)

}