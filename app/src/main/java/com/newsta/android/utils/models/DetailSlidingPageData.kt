package com.newsta.android.utils.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailSlidingPageData (
    val story: Story,
    val eventId: Int
) : Parcelable