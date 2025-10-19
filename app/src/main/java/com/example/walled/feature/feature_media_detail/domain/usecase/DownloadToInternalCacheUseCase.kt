package com.example.walled.feature.feature_media_detail.domain.usecase

import android.net.Uri
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository
import com.example.walled.util.Logger
import org.koin.core.annotation.Named

class DownloadToInternalCacheUseCase(
    private val repository : MediaDetailRepository
) {
    private val logger = Logger("DownloadToInternalCacheUseCase")
    suspend operator fun invoke(url:String): Result<Uri> {

        return repository.downloadToInternalCache(url)


//        val downloadResult = repository.downloadToInternalCache(url)
//         return when(downloadResult){
//            is Result.Success ->{
//                val uri = downloadResult.data
////                if(uri!=null){
////                    val result = repository.applyWallpaper(uri)
////                    when(result){
////                        is Result.Error -> {
////                            Result.Error(result.error)
////                        }
////                        is Result.Success -> {
////                            logger.info("Wallpaper Applied")
////                            Result.Success("Wallpaper applied")
////                        }
////                    }
////                }else{
////                    Result.Error(Exception("Uri is null"))
////                }
//                return Result.Success(uri)
//            }
//
//            is Result.Error -> {
//                Result.Error(downloadResult.error)
//            }
//        }
    }
}