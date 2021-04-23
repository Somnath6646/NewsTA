package com.newsta.android.ui.landing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.NewsItemBinding
import com.newsta.android.utils.models.Data
import com.squareup.picasso.Picasso

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>() {

    private val stories = ArrayList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    fun addAll(storiesResponse: ArrayList<Data>) {
        stories.clear()
        stories.addAll(storiesResponse)
        notifyDataSetChanged()
    }

}

class NewsViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Data) {

        binding.title.text = data.title
        binding.sources.text = "${data.numArticles.toString()} sources"
        binding.time.text = data.updatedAt

        Picasso.get()
            .load(data.imgUrl)
            .into(binding.image)

        println("Width = ${binding.image.width}")

    }

}
