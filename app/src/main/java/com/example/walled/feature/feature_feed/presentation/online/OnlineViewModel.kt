package com.example.walled.feature.feature_feed.presentation.online

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import com.example.walled.util.Logger
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.launch

class OnlineViewModel(
    private val feedUseCase: FeedUseCase
) : ViewModel() {


    private val _imageList = MutableLiveData<List<Media>>(emptyList<Media>())
    val imageList: LiveData<List<Media>> = _imageList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val logger = Logger("OnlineViewModel")


    private val _error = MutableLiveData<String>("error while fetching")
    val error: LiveData<String> = _error

    init {
        onEvent(FeedEvent.Fetch)
    }


    fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.Fetch -> {
                // fetch
                viewModelScope.launch {
                    _isLoading.postValue(true)
                    val response = feedUseCase.getRemoteImagesUseCase()
                    when (response) {
                        is Result.Error -> {
                            _error.postValue(
                                if (response.error is UnresolvedAddressException) {
                                    logger.error("Internet Turned Off")
                                    "Internet Turned Off"
                                } else {
                                    "Error while fetching"
                                }
                            )

                        }

                        is Result.Success -> {
                            _imageList.postValue(response.data as List<Media>)
                        }
                    }
                    _isLoading.postValue(false)
                }
            }

        }
    }

//    data class UiState<T>(
//        val data  : T? = null,
//        val error : Throwable? = null,
//        val isLoading : Boolean = false
//    )


}