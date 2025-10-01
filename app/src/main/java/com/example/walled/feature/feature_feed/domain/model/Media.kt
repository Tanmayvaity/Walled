package com.example.walled.feature.feature_feed.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    @SerialName("id")
    val photoId : String,
    @SerialName("created_at")
    val createdAt : String,
    @SerialName("updated_at")
    val updatedAt : String,
    val width : Int,
    val height : Int,
    @SerialName("blur_hash") val blurHash : String,
    val likes : Int,
    val description : String?,
    @SerialName("alt_description")val altDescription : String?,
    val urls : MediaUrl,
    val links : MediaLink,
    val user : MediaUserDetails
)

@Serializable
data class MediaLocation(
    val city : String,
    val country : String,
    val position : MediaLatLng
)

@Serializable
data class MediaLatLng(
    val latitude : Long,
    val longitude : Long
)

@Serializable
data class MediaTag(
    val title : String,
)


@Serializable
data class MediaUrl(
    val raw : String,
    val full : String,
    val regular : String,
    val small : String,
    val thumb : String
)

@Serializable
data class MediaLink(
    val self : String,
    val html : String,
    val download : String,
    @SerialName("download_location")
    val downloadLocation : String
)

@Serializable
data class MediaUserDetails(
    @SerialName("id")
    val userId : String,
    val username : String,
    val name : String,
)


