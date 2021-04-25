package com.newsta.android.ui.landing.adapter

import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.ItemTimelineEventsBinding
import com.newsta.android.utils.models.Event
import java.lang.String.format
import java.util.*
import kotlin.collections.ArrayList

class TimelineAdapter : RecyclerView.Adapter<TimelineViewHolder>() {

    private val events = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemTimelineEventsBinding>(inflater, R.layout.item_timeline_events , parent, false)
        return TimelineViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun addAll(eventList: ArrayList<Event>) {
        events.clear()
        events.addAll(eventList)
        notifyDataSetChanged()
    }

}

class TimelineViewHolder(private val binding: ItemTimelineEventsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {

        binding.titleEventsTimeline.text = event.title

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = event.updatedAt

        val date = "${calendar.get(Calendar.DAY_OF_MONTH)} ${getMonth(calendar.get(Calendar.MONTH))}"

        binding.dateEventsTimeline.text = date

    }

    private fun getMonth(month: Int) =
        when(month) {
            0 -> "Jan"
            1 -> "Feb"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dec"
            else -> "Jan"
        }

}
