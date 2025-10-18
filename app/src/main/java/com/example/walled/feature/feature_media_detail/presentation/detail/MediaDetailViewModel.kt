package com.example.walled.feature.feature_media_detail.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_media_detail.domain.usecase.MediaDetailUseCase
import kotlinx.coroutines.launch

class MediaDetailViewModel(
    private val mediaUseCase : MediaDetailUseCase
) : ViewModel(){
    private val _media  = MutableLiveData<Media>()
    val media : LiveData<Media> = _media

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean> = _isLoading


    fun onEvent(event : MediaDetailEvent){
        when(event){
            is MediaDetailEvent.FetchMedia -> {
                viewModelScope.launch {
                    _isLoading.postValue(true)
                    val response = mediaUseCase.getMediaByIdUseCase(event.id)
                    _media.postValue(response)
                    _isLoading.postValue(false)
                }
            }

            is MediaDetailEvent.DownloadMedia -> {
                viewModelScope.launch {
                    mediaUseCase.downloadMediaUseCase(event.id,event.url)
                }
            }
        }

    }
}