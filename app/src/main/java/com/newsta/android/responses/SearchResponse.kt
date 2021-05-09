package com.newsta.android.responses

data class SearchResponse(
    val `data`: List<SearchStory>,
    val status_code: Int
)