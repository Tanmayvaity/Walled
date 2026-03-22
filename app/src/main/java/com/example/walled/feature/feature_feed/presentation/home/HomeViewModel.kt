package com.example.walled.feature.feature_feed.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.core.domain.model.Image
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import com.example.walled.util.Logger
import kotlinx.coroutines.launch

class HomeViewModel(
    private val feedUseCase: FeedUseCase
) : ViewModel() {

    private val _localImageList = MutableLiveData<List<Image>>(emptyList())
    val localImageList: LiveData<List<Image>> = _localImageList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val logger = Logger("HomeViewModel")

    fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.Fetch -> {
                viewModelScope.launch {
                    _isLoading.postValue(true)
                    _error.postValue(null)
                    logger.debug("Fetch Event Triggered from ${logger.tag}")
                    val response = feedUseCase.getLocalImagesUseCase()
                    when (response) {
                        is Result.Error -> {
                            logger.error(response.error.message.toString())
                            _error.postValue(response.error.message)
                        }

                        is Result.Success -> {
                            @Suppress("UNCHECKED_CAST")
                            val images = response.data as List<Image>
                            _localImageList.postValue(images)
                            logger.debug("Fetched ${images.size} local images")
                        }
                    }
                    _isLoading.postValue(false)
                }
            }
        }
    }
}