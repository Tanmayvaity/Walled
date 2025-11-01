package com.example.walled.feature.feature_feed.domain.repository


import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


interface FeedRepository{
    suspend fun  <T : Any>fetchMedia(type : KClass<T>) : Result<List<T>>
    suspend fun downloadMedia()

}

//suspend inline fun <reified T : Any> FeedRepository.fetch(): Result<List<T>> {
//    return fetchMedia<T>()
//}

