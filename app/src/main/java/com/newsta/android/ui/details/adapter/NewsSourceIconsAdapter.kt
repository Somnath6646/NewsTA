package com.newsta.android.ui.details.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.SourcesIconItemBinding
import com.newsta.android.databinding.SourcesItemBinding
import com.newsta.android.utils.models.NewsSource
import com.squareup.picasso.Picasso
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "NewsSourceIconsAdapter"
class NewsSourceIconsAdapter() : RecyclerView.Adapter<NewsSourceIconsViewHolder>() {

    private var sources = ArrayList<NewsSource>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsSourceIconsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SourcesIconItemBinding>(inflater, R.layout.sources_icon_item, parent, false)
        return NewsSourceIconsViewHolder(binding)
    }

    override fun getItemCount(): Int = if(sources.size <= 7) sources.size else 7

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: NewsSourceIconsViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    fun addAll(sourcesResponse: ArrayList<NewsSource>): Boolean {

        sources.clear()
        sources.addAll(sourcesResponse.distinct())
        sources = ArrayList(sources.distinct())
        notifyDataSetChanged()
        return true
    }

}

class NewsSourceIconsViewHolder(private val binding: SourcesIconItemBinding) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun bind(source: NewsSource) {

        if(source.urlIcon.isNotEmpty()) {
            Picasso.get()
                .load(source.urlIcon)
                .into(binding.image)
        } else {
            binding.image.visibility = View.GONE
            binding.sourceNameLetter.visibility = View.VISIBLE
            binding.sourceNameLetter.text = source.name[0].toString().capitalize(Locale.ROOT)
            val random = Random().nextInt(4)
            binding.sourceNameLetter.background =
                ColorDrawable(Color.parseColor(NewstaApp.SOURCE_ICON_COLORS[random]))
        }

    }

}
