package com.newsta.android.ui.authentication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.ItemTutorialBinding
import com.newsta.android.utils.models.TutorialData

class TutorialAdapter() : RecyclerView.Adapter<TutorialViewHolder>() {

    private val list = arrayListOf<TutorialData>(
        TutorialData("Stay updated on the news with our Timeline.", R.drawable.timeline),
        TutorialData("Stay updated on the news with our Timeline.", R.drawable.timeline),
        TutorialData("Stay updated on the news with our Timeline.", R.drawable.timeline)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemTutorialBinding>(inflater, R.layout.item_tutorial, parent, false)
        return TutorialViewHolder(binding)
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.bind(list[position])
    }

}

class TutorialViewHolder(private val binding: ItemTutorialBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tutorialData: TutorialData) {
        binding.imageDemo.setImageResource(tutorialData.image)
        binding.textDemo.text = tutorialData.text
    }

}
