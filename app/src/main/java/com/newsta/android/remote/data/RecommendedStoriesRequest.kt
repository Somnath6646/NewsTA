package com.newsta.android.remote.data

import com.google.android.gms.common.util.CollectionUtils.listOf


data class RecommendedStoriesRequest(
    val access_token: String,
    val iss: String,
    val storyIDs: List<Int> = listOf()
)