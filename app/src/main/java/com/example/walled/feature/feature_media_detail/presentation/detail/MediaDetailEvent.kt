package com.example.walled.feature.feature_media_detail.presentation.detail

import android.net.Uri

sealed class MediaDetailEvent {
    data class FetchMedia(val id : String): MediaDetailEvent()
    data class DownloadMedia(val id : String,val url :String) : MediaDetailEvent()

    data class SetWallpaper(val uri : Uri) : MediaDetailEvent()

    data class DownloadToInternalCache(val url : String) : MediaDetailEvent()

}