package com.example.walled.feature.feature_feed.presentation

sealed class FeedEvent {
    object Fetch : FeedEvent()
}