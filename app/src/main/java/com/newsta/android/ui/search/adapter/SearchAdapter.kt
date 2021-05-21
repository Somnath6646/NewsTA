package com.newsta.android.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.ItemSearchresultBinding
import com.newsta.android.responses.SearchStory
import com.squareup.picasso.Picasso

class SearchAdapter(private val onClick: (SearchStory, Int) -> Unit) : RecyclerView.Adapter<SearchViewHolder>() {

    private var stories = ArrayList<SearchStory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemSearchresultBinding>(inflater, R.layout.item_searchresult, parent, false)
        return SearchViewHolder(
            binding,
            onClick
        )
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<SearchStory>) {
        stories.clear()
        stories.addAll(storiesResponse)
        notifyDataSetChanged()
    }

}

class SearchViewHolder(private val binding: ItemSearchresultBinding, private val onClick: (SearchStory, Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: SearchStory) {


        val event = story.events.first()

        binding.title.text = event.title
        
        binding.time.text = "${NewstaApp.setTime(story.created_at)}"

        Picasso.get()
                .load(event.img_url)
                .into(binding.image)

        binding.root.setOnClickListener { onClick(story , event.event_id) }

    }

}