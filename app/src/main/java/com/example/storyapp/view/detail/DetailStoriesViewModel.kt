package com.example.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.result.ResultState
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.response.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DetailStoriesViewModel(private val repository: Repository): ViewModel() {
    private val _detailStories = MutableLiveData<ResultState<Story?>>()
    val detailStories : LiveData<ResultState<Story?>> = _detailStories

    val getSession: Flow<String?> = repository.getSession()

    fun getDetailStories(id: String){
        viewModelScope.launch {
            repository.getSession().collect{token->
                if(token != null){
                    _detailStories.value = ResultState.Loading
                    try {
                        val response = repository.getDetailStories(token,id)
                        _detailStories.value = ResultState.Success(response.story)
                    }catch (e: Exception){
                        _detailStories.value = ResultState.Error(e.message.toString())
                    }
                }
            }
        }
    }
}