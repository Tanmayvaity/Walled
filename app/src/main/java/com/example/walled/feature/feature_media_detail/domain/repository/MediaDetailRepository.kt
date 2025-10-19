package com.example.walled.feature.feature_media_detail.domain.repository


import android.net.Uri
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result

interface MediaDetailRepository {
    suspend fun fetchMedia(id : String) : Media
    suspend fun download(id : String,url:String)

    suspend fun downloadToInternalCache(url : String) : Result<Uri>

    suspend fun applyWallpaper(uri : Uri) : Result<String>


}