package com.newsta.android.ui.recommended.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.BuildConfig
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.RecommendedStory
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso

class RecommendedStoriesAdapter(private val onClick: (Int) -> Unit) : RecyclerView.Adapter<RecommendedStoriesViewHolder>() {

    private lateinit var onDataSetChangeListener: OnDataSetChangedListener

    var stories = ArrayList<RecommendedStory>()
        private set

    fun setDataSetChangeListener(onDataSetChangeListener: OnDataSetChangedListener){
        this.onDataSetChangeListener = onDataSetChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedStoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return RecommendedStoriesViewHolder(binding, onClick)
    }


    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: RecommendedStoriesViewHolder, position: Int) {
        holder.bind(stories[position], position, stories)
    }

    fun addAll(storiesList: ArrayList<RecommendedStory>) {
        stories.clear()
        stories.addAll(storiesList)
        stories = ArrayList(stories.distinct())
        println("LIST SIZE ${stories.size}")
        notifyDataSetChanged()
        onDataSetChangeListener.onDataSetChange(convertStoryType(storiesList))
    }

    fun clear() {
        stories.clear()
    }

    fun convertStoryType(stories: List<RecommendedStory>): ArrayList<Story>{
        val newStories: ArrayList<Story> = ArrayList()
        stories.forEach {story ->
            newStories.add(
                Story(
                    storyId = story.storyId,
                    events = story.events,
                    category = story.category,
                    updatedAt = story.updatedAt,
                    viewCount = 0
                ))
        }
        return newStories
    }


}

class RecommendedStoriesViewHolder(private val binding: NewsItemBinding, private val onClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: RecommendedStory, position: Int, stories: List<RecommendedStory>) {

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

        binding.root.setOnClickListener { onClick(position) }

    }

}
