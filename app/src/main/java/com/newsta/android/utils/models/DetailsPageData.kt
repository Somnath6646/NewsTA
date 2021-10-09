package com.newsta.android.utils.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailsPageData (
    val position: Int,
    val selectedEventId: Int
) : Parcelable