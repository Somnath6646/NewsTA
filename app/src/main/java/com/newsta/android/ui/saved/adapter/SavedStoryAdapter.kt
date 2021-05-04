package com.newsta.android.ui.saved.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso

private var category = 0

class SavedStoryAdapter(private val onClick: (SavedStory) -> Unit, private val onLongClick: (SavedStory) -> Boolean) : RecyclerView.Adapter<SavedStoryViewHolder>() {

    private val stories = ArrayList<SavedStory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedStoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return SavedStoryViewHolder(binding, onClick, onLongClick)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: SavedStoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<SavedStory>) {
        stories.clear()
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

class SavedStoryViewHolder(private val binding: NewsItemBinding, private val onClick: (SavedStory) -> Unit, private val onLongClick: (SavedStory) -> Boolean) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: SavedStory) {

        val events = story.events.sortedByDescending { events -> events.updatedAt }

        val event = events.last()

        binding.title.text = event.title
        binding.timeline.text = if(events.size > 1) "View timeline" else ""
        binding.time.text = NewstaApp.setTime(story.updatedAt)

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.image)

        binding.root.setOnClickListener { onClick(story) }

        binding.root.setOnLongClickListener { onLongClick(story) }

    }

}
