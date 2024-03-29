package com.newsta.android.ui.landing.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.Story
import com.newsta.android.BuildConfig
import com.squareup.picasso.Picasso


class NewsAdapter(private val onClick: (Int, List<Story>) -> Unit) : RecyclerView.Adapter<NewsViewHolder>() {

    private lateinit var onDataSetChangeListener: OnDataSetChangedListener

    var stories = ArrayList<Story>()
    private set

    fun setDataSetChangeListener(onDataSetChangeListener: OnDataSetChangedListener){
        this.onDataSetChangeListener = onDataSetChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NewsViewHolder(binding, onClick)
    }


    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(stories[position], position, stories)
    }

    fun addAll(storiesList: ArrayList<Story>) {
        stories.clear()
        stories.addAll(storiesList)
        stories = ArrayList(stories.distinct())
        println("LIST SIZE ${stories.size}")
        notifyDataSetChanged()
        onDataSetChangeListener.onDataSetChange(stories)
    }


    fun refreshAdd(storiesList: ArrayList<Story>) {

        val newStories = storiesList

        storiesList.forEach { newStory ->
            if(stories.contains(newStory)) {
                println("OLD STORIES SIZE BEF REM: ${stories.size}")
                stories.remove(newStory)
                // stories.removeIf { oldStory -> oldStory.storyId == newStory.storyId }
                println("OLD STORIES SIZE AFT REM: ${stories.size}")
            }
        }

        stories.addAll(0, storiesList)

        stories = ArrayList(stories.distinct())

        notifyDataSetChanged()
        onDataSetChangeListener.onDataSetChange(stories)

    }

    fun clear() {
        stories.clear()
    }

}

class NewsViewHolder(private val binding: NewsItemBinding, private val onClick: (Int, List<Story>) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: Story, position: Int, stories: List<Story>) {

        story.events = story.events.sortedBy { it.createdAt }
        val event =story.events.last()

        binding.title.text = event.title
        if (story.events.size > 1){
            binding.timelineIndicator.visibility = View.VISIBLE
        }else{
            binding.timelineIndicator.visibility = View.INVISIBLE
        }
        binding.time.text = NewstaApp.setTime(story.updatedAt)
        if (BuildConfig.DEBUG) {
            binding.time.text =
                "${NewstaApp.setTime(story.updatedAt)} - ${story.storyId} - ${story.category}"
        }

        if (!event.imgUrl.isNullOrEmpty()) {

            Picasso.get()
                .load(event.imgUrl)
                .into(binding.image)

        } else {
            binding.image.setImageResource(R.drawable.newsta_default)
        }

        binding.root.setOnClickListener { onClick(position, stories) }

    }

}
