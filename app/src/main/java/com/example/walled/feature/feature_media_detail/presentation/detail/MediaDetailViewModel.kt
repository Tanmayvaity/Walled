package com.example.walled.feature.feature_media_detail.presentation.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_media_detail.domain.usecase.MediaDetailUseCase
import kotlinx.coroutines.launch

class MediaDetailViewModel(
    private val mediaUseCase : MediaDetailUseCase
) : ViewModel() {
    private val _media = MutableLiveData<Media>()
    val media: LiveData<Media> = _media

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _tempImageUri = MutableLiveData<Uri?>()
    val tempImageUri: LiveData<Uri?> = _tempImageUri


    fun onEvent(event: MediaDetailEvent) {
        when (event) {
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
                    mediaUseCase.downloadMediaUseCase(event.id, event.url)
                }
            }

            is MediaDetailEvent.DownloadToInternalCache -> {
                viewModelScope.launch {
                    val result = mediaUseCase.downloadToInternalCacheUseCase(event.url)
                    when (result) {
                        is Result.Error -> {

                        }

                        is Result.Success -> {
                            _tempImageUri.postValue(result.data)

                        }
                    }
                }
            }

            is MediaDetailEvent.SetWallpaper -> {
                viewModelScope.launch {
                    val result = mediaUseCase.applyWallpaperUseCase(event.uri)

                }


            }

        }
    }
}