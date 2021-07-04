package com.newsta.android.utils.helpers

import com.newsta.android.utils.models.Story

interface OnDataSetChangedListener {
    fun onDataSetChange(stories: List<Story>)
}