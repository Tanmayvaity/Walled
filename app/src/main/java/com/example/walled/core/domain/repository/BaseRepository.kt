package com.example.walled.core.domain.repository

import com.example.walled.core.domain.model.Result

interface BaseRepository{
    suspend fun <T>fetchMedia(): Result<T>
    suspend fun download(id : String,url:String)
}