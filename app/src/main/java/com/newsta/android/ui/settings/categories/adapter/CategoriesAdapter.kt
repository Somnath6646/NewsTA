package com.newsta.android.ui.settings.categories.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.CategoriesItemBinding
import com.newsta.android.utils.models.Category
import java.util.*
import kotlin.collections.ArrayList

class CategoriesAdapter(private val startDragging: (RecyclerView.ViewHolder) -> Unit) : RecyclerView.Adapter<CategoriesViewHolder>() {

    private val categories = ArrayList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<CategoriesItemBinding>(
            inflater,
            R.layout.categories_item,
            parent,
            false
        )

        return CategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.binding.reorder.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                startDragging(holder)
            }
            return@setOnTouchListener true
        }
        holder.bind(categories[position])
    }

    fun addAll(categoriesList: ArrayList<Category>): Boolean {
        categoriesList.distinct()
        categories.clear()
        categories.addAll(categoriesList)
        notifyDataSetChanged()
        return true
    }

    fun moveItem(from: Int, to: Int) {
        println("TO: $to FROM: $from")
        val oldCategory = categories[from]
        categories.removeAt(from)
        categories.add(to, oldCategory)
        println("NEW CATEGORIES ARRAY: $categories")
    }

}

class CategoriesViewHolder(val binding: CategoriesItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category) {
        binding.textViewCategory.text = category.category.capitalize(Locale.ROOT)
    }

}
