package com.newsta.android.ui.saved.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso

private var category = 0

class SavedStoryAdapter(private val onClick: (SavedStory) -> Unit) : RecyclerView.Adapter<SavedStoryViewHolder>() {

    private val stories = ArrayList<SavedStory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedStoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return SavedStoryViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: SavedStoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<SavedStory>) {
        storiesResponse.sortByDescending {
                story ->  story.updatedAt
        }
        stories.addAll(storiesResponse)
        println("LIST SIZE ${stories.size}")
        notifyDataSetChanged()
    }

    fun setCategory(categoryState: Int) {
        category = categoryState
        notifyDataSetChanged()
    }

}

class SavedStoryViewHolder(private val binding: NewsItemBinding, private val onClick: (SavedStory) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: SavedStory) {

        val events = story.events.sortedByDescending { events -> events.updatedAt }

        val event = events.last()

        binding.title.text = event.title
        binding.timeline.text = if(events.size > 1) "View timeline" else ""
        binding.time.text = setTime(story.updatedAt)

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.image)

        binding.root.setOnClickListener { onClick(story) }

    }

    private fun setTime(updatedAt: Long): String {

        val time = System.currentTimeMillis()

        val diff = time - updatedAt

        val seconds: Long = diff / 1000

        val minutes: Int = (seconds / 60).toInt()

        val hours: Int = minutes / 60
        val days: Int = hours / 24
        val months: Int = days / 30
        val years: Int = months / 12

        if (minutes <= 30) {
            return "Few minutes ago"
        }
        else if (minutes > 30 && minutes < 60) {
            return "Less than an hour ago"
        } else {

            if (hours == 1)
                return "An hour ago"
            else if (hours > 1 && hours < 24) {
                return "$hours hours ago"
            } else {
                if (days >= 1 && days < 30) {
                    return "$days days ago"
                } else {
                    if (months >= 1 && months < 12) {
                        return "$months months ago"
                    } else {
                        if (years == 1)
                            return "An year ago"
                        else if (years > 1)
                            return "$years years ago"
                        else
                            return "Time unknown"
                    }
                }
            }

        }

    }

}
