package com.newsta.android.ui.notify.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.remote.data.Payload
import com.newsta.android.ui.details.adapter.pos
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso

class NotifyStoriesAdapter(private val onClick: (Int) -> Unit) : RecyclerView.Adapter<NotifyStoriesViewHolder>() {

    private lateinit var onDataSetChangeListener: OnDataSetChangedListener

    private var stories = ArrayList<SavedStory>()
    private val payloads = ArrayList<Payload>()

    fun setDataSetChangeListener(onDataSetChangeListener: OnDataSetChangedListener){
        this.onDataSetChangeListener = onDataSetChangeListener
    }

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyStoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NotifyStoriesViewHolder(binding, onClick)
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: NotifyStoriesViewHolder, position: Int) {
        tracker.let {
            if (it != null) {
                val story = stories[position]
                holder.bind(story, payloads.get(payloads.indexOf(Payload(0,  story.storyId))) , it.isSelected(position.toLong() ), position)
            }
        }
    }

    fun addAll(storiesResponse: ArrayList<SavedStory>, payloads: ArrayList<Payload>) {
        if(!stories.equals(storiesResponse.distinct())){
        stories.clear()
        this.payloads.clear()
        this.payloads.addAll(payloads.distinct())
        stories.addAll(storiesResponse.distinct())
        if(!stories.isNullOrEmpty())
        stories = ArrayList(stories.distinctBy { it.storyId })
        Log.i("12246 list size", "${stories.size}")
        println("LIST SIZE ${stories.size}")
        notifyDataSetChanged()

        onDataSetChangeListener.onDataSetChange(stories = convertStoryType(stories))}
        else{
            Log.i("12245 notify id list", "not added")
        }
    }

    fun convertStoryType(stories: List<SavedStory>): ArrayList<Story>{
        val newStories: ArrayList<Story> = ArrayList()
        stories.forEach {story ->
            newStories.add(
                Story(
                    storyId = story.storyId,
                    events = story.events,
                    category = story.category,
                    updatedAt = story.updatedAt,
                    viewCount = story.viewCount
                )
            )
        }
        return newStories
    }

}

class NotifyStoriesViewHolder(private val binding: NewsItemBinding, private val onClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: SavedStory, payload: Payload, isSelected: Boolean = false, position: Int) {

        val events = story.events.sortedByDescending { events -> events.updatedAt }

        val event = events.last()

        binding.title.text = event.title
        binding.timeline.text = if(events.size > 1) "View timeline" else ""
        binding.time.text = NewstaApp.setTime(story.updatedAt)

        if(payload.read == ArticleState.READ){
            binding.title.setTextColor(Color.GRAY)
        }

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.image)

        binding.root.setOnClickListener {
            Log.i("12246 indexOut", "$position")
            onClick(story.storyId) }

        val checkLayout = binding.checkLayout

        if (isSelected){
            checkLayout.visibility = View.VISIBLE
        }else{
            checkLayout.visibility = View.GONE
        }
    }


    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =

        object : ItemDetailsLookup.ItemDetails<Long>() {

            override fun getPosition(): Int = adapterPosition

            override fun getSelectionKey(): Long? = itemId

        }

}