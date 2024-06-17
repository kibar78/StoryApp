package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel(){

    private val _listStories = MutableLiveData<ResultState<List<ListStoryItem?>>>()
    val listStories : LiveData<ResultState<List<ListStoryItem?>>> = _listStories

    val getSession: Flow<String?> = repository.getSession()

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(){
        viewModelScope.launch {
            repository.getSession().collect { token ->
                if (token != null) {
                    _listStories.value = ResultState.Loading
                    try {
                        val response = repository.getAllStories(token)
                        _listStories.value = ResultState.Success(response.listStory!!)
                    } catch (e: Exception) {
                        _listStories.value = ResultState.Error(e.message.toString())
                    }
                }
            }
        }
    }
}