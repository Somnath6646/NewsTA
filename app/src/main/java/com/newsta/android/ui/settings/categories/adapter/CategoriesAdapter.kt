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
import com.newsta.android.utils.models.UserCategory
import java.util.*
import kotlin.collections.ArrayList

class CategoriesAdapter(private val startDragging: (RecyclerView.ViewHolder) -> Unit, private val onCategoryChange: (UserCategory, Boolean) -> Unit, private val onCategoryPositionChange: (Int, Int) -> Unit) : RecyclerView.Adapter<CategoriesViewHolder>() {

    private val categories = ArrayList<Category>()
    private var userCategories = ArrayList<UserCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<CategoriesItemBinding>(
            inflater,
            R.layout.categories_item,
            parent,
            false
        )

        return CategoriesViewHolder(binding, onCategoryChange)
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
        holder.bind(userCategories[position])
    }

    fun addAll(categoriesList: ArrayList<Category>, userCategoriesList: ArrayList<UserCategory>): Boolean {
        categoriesList.distinct()
        userCategoriesList.distinct()
        categories.clear()
        categories.addAll(categoriesList)
        userCategories.clear()
        userCategories.addAll(userCategoriesList)
        notifyDataSetChanged()
        return true
    }

    fun moveItem(from: Int, to: Int) {
        println("TO: $to FROM: $from")
        val oldCategory = userCategories[from]
        userCategories.removeAt(from)
        userCategories.add(to, oldCategory)
        onCategoryPositionChange(from, to)
        println("NEW CATEGORIES ARRAY: $categories")
    }

}

class CategoriesViewHolder(val binding: CategoriesItemBinding, private val onCategoryChange: (UserCategory, Boolean) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: UserCategory) {
        binding.textViewCategory.text = category.category.capitalize(Locale.ROOT)
            binding.switchCategory.isChecked = category.isEnabled

        binding.switchCategory.setOnCheckedChangeListener { buttonView, isChecked ->
            onCategoryChange(category, isChecked)
        }
    }

}
