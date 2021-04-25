package com.newsta.android.ui.landing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.ItemTimelineEventsBinding
import com.newsta.android.utils.models2.NewsItem
import com.squareup.picasso.Picasso

class TimelineAdapter : RecyclerView.Adapter<TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemTimelineEventsBinding>(inflater, R.layout.item_timeline_events , parent, false)
        return TimelineViewHolder(binding)
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {

    }



}

class TimelineViewHolder(private val binding: ItemTimelineEventsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: NewsItem) {



    }

}
