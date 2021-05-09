package com.newsta.android.responses

data class SearchStory(
    val created_at: Long,
    val events: List<SearchEvent>,
    val story_id: Int
)