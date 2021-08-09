package com.newsta.android.utils



import android.view.MotionEvent

import androidx.recyclerview.selection.ItemDetailsLookup

import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.ui.notify.adapter.NotifyStoriesViewHolder
import com.newsta.android.ui.saved.adapter.SavedStoryViewHolder


class SavedStoryItemDetailsLookup(private val recyclerView: RecyclerView) :

    ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {

        val view = recyclerView.findChildViewUnder(event.x, event.y)

        if (view != null) {

            return (recyclerView.getChildViewHolder(view) as SavedStoryViewHolder)

                .getItemDetails()

        }

        return null

    }

}

class NotifyStoryItemDetailsLookup(private val recyclerView: RecyclerView) :

    ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {

        val view = recyclerView.findChildViewUnder(event.x, event.y)

        if (view != null) {

            return (recyclerView.getChildViewHolder(view) as NotifyStoriesViewHolder)

                .getItemDetails()

        }

        return null

    }

}