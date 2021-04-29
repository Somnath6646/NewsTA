package com.newsta.android.ui.landing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class NewsAdapter(private val onClick: (Story) -> Unit) : RecyclerView.Adapter<NewsViewHolder>() {

    private val stories = ArrayList<Story>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NewsViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<Story>): Boolean {
        stories.clear()
        storiesResponse.sortBy {
            story ->  story.storyId
        }
        stories.addAll(storiesResponse)
        notifyDataSetChanged()
        return true
    }

}

class NewsViewHolder(private val binding: NewsItemBinding, private val onClick: (Story) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: Story) {

        var max=0L
        var indexOfEvent = story.events.size - 1
        for (eventIndex in story.events.indices){
            val event = story.events[eventIndex]
            if(event.updatedAt > max){
                max = event.updatedAt
                indexOfEvent = eventIndex
            }
        }

        val event = story.events[indexOfEvent]

        binding.title.text = event.title
        binding.sources.text = "${event.numArticles.toString()} sources"
        binding.time.text = event.updatedAt.toString()

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.image)

        setDate(event.updatedAt)

        println("Width = ${binding.image.width}")

        binding.root.setOnClickListener { onClick(story) }

    }

    private fun setDate(millis: Long) {

        val updatedAt = Calendar.getInstance()
        
        updatedAt.timeInMillis = millis

        val calendar = Calendar.getInstance()

        println(" YEAR:  ${updatedAt.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)} ${updatedAt.get(Calendar.YEAR)} ${calendar.get(Calendar.YEAR)}")
        println(" MONTH:  ${updatedAt.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)}  ${updatedAt.get(Calendar.MONTH)} ${calendar.get(Calendar.MONTH)}")
        println(" DAY_OF_MONTH:  ${updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)} ${updatedAt.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.DAY_OF_MONTH)}")

        if (updatedAt.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {

            if (updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {

                if (updatedAt.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)) {

                    println("${calendar.get(Calendar.MINUTE)} - ${updatedAt.get(Calendar.MINUTE)}")

                    val minutes =
                        Math.abs(calendar.get(Calendar.MINUTE) - updatedAt.get(Calendar.MINUTE))

                    binding.time.text = "$minutes minutes ago"

                } else {

                    println("${calendar.get(Calendar.HOUR_OF_DAY)} - ${updatedAt.get(Calendar.HOUR_OF_DAY)}")

                    val hours = Math.abs(
                        calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)
                    )

                    binding.time.text = "$hours hours ago"

                }

            } else if (updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) - 1) {


                val hours =
                    calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)

                if (hours > 0)
                    binding.time.text = "$hours minutes ago"
                else {
                    binding.time.text = "1 day ago"
                }

            } else {
                val days =
                    calendar.get(Calendar.DAY_OF_MONTH) - updatedAt.get(Calendar.DAY_OF_MONTH)

                binding.time.text = "$days days ago"
            }

        } else {

            val months =
                calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)

            binding.time.text = "$months months ago"

        }

    }

}
