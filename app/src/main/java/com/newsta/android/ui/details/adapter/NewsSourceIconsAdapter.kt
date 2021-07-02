package com.newsta.android.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.SourcesIconItemBinding
import com.newsta.android.databinding.SourcesItemBinding
import com.newsta.android.utils.models.NewsSource
import com.squareup.picasso.Picasso
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewsSourceIconsAdapter() : RecyclerView.Adapter<NewsSourceIconsViewHolder>() {

    private val sources = ArrayList<NewsSource>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsSourceIconsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SourcesIconItemBinding>(inflater, R.layout.sources_icon_item, parent, false)
        return NewsSourceIconsViewHolder(binding)
    }

    override fun getItemCount(): Int = if(sources.size <= 7) sources.size else 7

    override fun onBindViewHolder(holder: NewsSourceIconsViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    fun addAll(sourcesResponse: ArrayList<NewsSource>): Boolean {
        sourcesResponse.distinct()
        sources.clear()
        sources.addAll(sourcesResponse)
        notifyDataSetChanged()
        return true
    }

}

class NewsSourceIconsViewHolder(private val binding: SourcesIconItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(source: NewsSource) {

        Picasso.get()
            .load(source.urlIcon)
            .into(binding.image)

    }

}
