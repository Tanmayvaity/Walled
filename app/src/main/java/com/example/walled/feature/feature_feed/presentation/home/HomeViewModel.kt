package com.example.walled.feature.feature_feed.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.core.domain.model.Image
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import com.example.walled.util.Logger
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.launch

class HomeViewModel(
    private val feedUseCase: FeedUseCase
) : ViewModel() {


    private val _localImageList = MutableLiveData<List<Media>>(emptyList<Media>())
    val localImageList: LiveData<List<Media>> = _localImageList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val logger = Logger("HomeViewModel")


    private val _error = MutableLiveData<String>("error while fetching")
    val error: LiveData<String> = _error

    init {
//        onEvent(FeedEvent.Fetch)
    }


    fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.Fetch -> {
                // fetch
                viewModelScope.launch {
                    _isLoading.postValue(true)
                    logger.debug("Fetch Event Triggered from ${logger.tag}")
                    val response = feedUseCase.getLocalImagesUseCase()
                    when (response) {
                        is Result.Error -> {
                            logger.error(response.error.message.toString())
                        }

                        is Result.Success -> {
//                            _imageList.postValue(response.data as List<Media>)
                            logger.debug(localImageList.toString())

                            (response.data as List<Image>).forEach { it ->
                                logger.debug(it.toString())
                            }

                        }
                    }
                    _isLoading.postValue(false)
                }
            }

        }
    }


}