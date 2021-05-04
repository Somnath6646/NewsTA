package com.newsta.android.ui.landing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso

private var category = 0

class NewsAdapter(private val onClick: (Story) -> Unit) : RecyclerView.Adapter<NewsViewHolder>() {

    private var stories = ArrayList<Story>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NewsViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<Story>) {
        storiesResponse.sortByDescending {
            story ->  story.updatedAt
        }
        val storiesList = storiesResponse.filter { story -> story.category == category }
        stories = ArrayList(storiesList)
        stories.addAll(storiesResponse)
        println("LIST SIZE ${stories.size}")
        notifyDataSetChanged()
    }

    fun setCategory(categoryState: Int) {
        category = categoryState
        notifyDataSetChanged()
    }

}

class NewsViewHolder(private val binding: NewsItemBinding, private val onClick: (Story) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: Story) {

        val events = story.events.sortedByDescending { events -> events.updatedAt }

        val event = events.last()

        binding.title.text = event.title
        binding.timeline.text = if(events.size > 1) "View timeline" else ""
        binding.time.text = "${NewstaApp.setTime(story.updatedAt)} - ${story.storyId}"

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.image)

        binding.root.setOnClickListener { onClick(story) }

    }

}
