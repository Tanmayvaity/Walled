package com.example.walled.feature.feature_feed.data.repository


import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.walled.core.data.source.MediaManager
import com.example.walled.core.domain.model.Image
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path
import kotlin.reflect.KClass

class OnlineFeedRepositoryImpl(
    val client: HttpClient,
    val mediaManager : MediaManager
) : FeedRepository {
    private val logger = Logger("OnlineFeedRepositoryImpl")
    override suspend fun<T : Any> fetchMedia(type : KClass<T>): Result<List<T>> {
        logger.debug("fetchMedia called in ${logger.tag}")

        @Suppress("UNCHECKED_CAST")
        return when(type){
            Media::class -> {
                logger.debug("fetching media called")
                try {
                    val response = client.get {
                        url {
                            path("photos")
                        }
                    }.body<List<Media>>()
                    Result.Success(response)
                }catch (e : Exception){
                    logger.error(e.message.toString())
                    e.printStackTrace()
                    Result.Error(e)
                }
            }
            Image::class ->{
                logger.debug("fetching images locally")
                mediaManager.getImages()
            }


            else -> Result.Error(Exception("Invalid Type"))

        } as Result<List<T>>


    }

    override suspend fun downloadMedia() {

    }


}