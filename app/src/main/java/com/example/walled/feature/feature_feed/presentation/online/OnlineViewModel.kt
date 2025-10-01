package com.example.walled.feature.feature_feed.presentation.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.feature.feature_feed.domain.model.Media
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import kotlinx.coroutines.launch

class OnlineViewModel(
    private val feedUseCase: FeedUseCase
) : ViewModel() {


    private val _imageList  = MutableLiveData<List<Media>>(emptyList<Media>())
    val imageList : LiveData<List<Media>> = _imageList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean> = _isLoading

    init {
        onEvent(FeedEvent.Fetch)
    }


    fun onEvent(event : FeedEvent){
        when(event){
            FeedEvent.Fetch ->{
                // fetch
                viewModelScope.launch {
                    _isLoading.postValue(true)
                    val response = feedUseCase.getRemoteImagesUseCase()
                    _imageList.postValue(response)
                    _isLoading.postValue(false)
                }
            }

        }
    }


}