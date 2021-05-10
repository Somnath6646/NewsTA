package com.newsta.android.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.SourcesItemBinding
import com.newsta.android.utils.models.NewsSource
import com.squareup.picasso.Picasso
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(source.createdAt)

        val sdf2 = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val time = sdf2.format(date?.time)

        binding.time.text = time

        binding.root.setOnClickListener { onClick(source) }

    }

}
