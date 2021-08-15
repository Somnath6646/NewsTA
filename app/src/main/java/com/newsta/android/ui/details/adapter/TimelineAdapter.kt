package com.newsta.android.ui.details.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.BuildConfig
import com.newsta.android.R
import com.newsta.android.databinding.ItemTimelineEventsBinding
import com.newsta.android.utils.models.Event
import java.util.*
import kotlin.collections.ArrayList

var size = 0
var pos = 0

class TimelineAdapter(private val onClick: (Event) -> Unit) : RecyclerView.Adapter<TimelineViewHolder>() {

    private val events = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemTimelineEventsBinding>(inflater, R.layout.item_timeline_events , parent, false)
        return TimelineViewHolder(
            binding,
            onClick
        )
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun addAll(eventList: ArrayList<Event>) {

        events.clear()
        events.addAll(eventList)
        events.sortBy {
            it.createdAt
        }
        size = events.size
        pos = 0
        notifyDataSetChanged()
    }

}

class TimelineViewHolder(private val binding: ItemTimelineEventsBinding, private val onClick: (Event) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {

        println("POSITION: $pos SIZE: $size")

        binding.titleEventsTimeline.text = event.title

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        calendar.timeInMillis = event.createdAt

        println("CURRENT YEAR: $currentYear")

        if (calendar.get(Calendar.YEAR) == currentYear) {
            val date =
                "${calendar.get(Calendar.DAY_OF_MONTH)} ${getMonth(calendar.get(Calendar.MONTH))}"
            binding.dateEventsTimeline.text = date
        } else {
            val date =
                "${calendar.get(Calendar.DAY_OF_MONTH)} ${getMonth(calendar.get(Calendar.MONTH))}, ${calendar.get(Calendar.YEAR)}"
            binding.dateEventsTimeline.text = date
        }

        if(BuildConfig.DEBUG){
            binding.dateEventsTimeline.text = binding.dateEventsTimeline.text.toString()+"\t" + event.eventId.toString()
        }
        
        if(pos == 0)
            binding.upperAttacherEventsTimeline.visibility = View.INVISIBLE

        pos++

        if(pos == size)
            binding.downAttacherEventsTimeline.visibility = View.INVISIBLE

        binding.root.setOnClickListener { onClick(event) }

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
