package com.example.walled.core.data.source

import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import com.example.walled.core.domain.model.Result
import com.example.walled.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WallpaperHelper(
    val context : Context
){
    private val wallpaperManager by lazy { WallpaperManager.getInstance(context) }
    private val logger = Logger("WallpaperHelper")

    suspend fun setWallpaper(uri : Uri) : Result<String>{
        return withContext(Dispatchers.IO){
            try {
                context.contentResolver.openInputStream(uri)?.use{ inputStream ->
                    wallpaperManager.setStream(inputStream)
                }
                logger.debug("Wallpaper applied successfully")
                Result.Success("Wallpaper applied successfully")
            }catch (e : Exception){
                logger.error("Failed to set wallpaper")
                Result.Error(e)
            }


        }

    }
}