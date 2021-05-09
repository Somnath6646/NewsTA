package com.newsta.android.utils.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailsPageData (
        val story: Story,
        val eventId: Int
) : Parcelable