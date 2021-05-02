package com.newsta.android.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.SourcesItemBinding
import com.newsta.android.utils.models.NewsSource
import com.squareup.picasso.Picasso

class NewsSourceAdapter(private val onClick: (NewsSource) -> Unit) : RecyclerView.Adapter<NewsSourceViewHolder>() {

    private val sources = ArrayList<NewsSource>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsSourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SourcesItemBinding>(inflater, R.layout.sources_item, parent, false)
        return NewsSourceViewHolder(
            binding,
            onClick
        )
    }

    override fun getItemCount(): Int = sources.size

    override fun onBindViewHolder(holder: NewsSourceViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    fun addAll(sourcesResponse: ArrayList<NewsSource>): Boolean {
        sources.clear()
        sources.addAll(sourcesResponse)
        notifyDataSetChanged()
        return true
    }

}

class NewsSourceViewHolder(private val binding: SourcesItemBinding, private val onClick: (NewsSource) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(source: NewsSource) {

        binding.title.text = source.title

        Picasso.get()
            .load(source.imgUrl)
            .into(binding.image)

        println("Width = ${binding.image.width}")

        binding.root.setOnClickListener { onClick(source) }

    }

    private fun setTime(updatedAt: Long): String {

        val time = System.currentTimeMillis()

        val diff = time - updatedAt

        println("DIFF:   $diff")
        println("TIME:   $time")
        println("UPDATED_AT:   $updatedAt")

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
