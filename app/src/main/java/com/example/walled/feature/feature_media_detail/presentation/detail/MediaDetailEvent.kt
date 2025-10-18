package com.example.walled.feature.feature_media_detail.presentation.detail

sealed class MediaDetailEvent {
    data class FetchMedia(val id : String): MediaDetailEvent()
    data class DownloadMedia(val id : String,val url :String) : MediaDetailEvent()

}