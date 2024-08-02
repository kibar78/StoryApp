package com.example.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class LocationViewModel(private val repository: Repository): ViewModel(){
    private val _location = MutableLiveData<ResultState<List<ListStoryItem?>>>()
    val location: LiveData<ResultState<List<ListStoryItem?>>> = _location

    val getSession: Flow<String?> = repository.getSession()

    fun getLocation(location: Int){
        viewModelScope.launch {
            repository.getSession().collect{token->
                if (token != null){
                    _location.value = ResultState.Loading
                 try {
                     val response = repository.getLocation(token,location)
                     _location.value = ResultState.Success(response.listStory!!)
                 }   catch (e: Exception){
                     _location.value = ResultState.Error(e.message.toString())
                }
                }
            }
        }
    }
}