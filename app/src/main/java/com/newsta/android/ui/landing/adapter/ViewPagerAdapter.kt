package com.newsta.android.ui.landing.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.landing.fragments.StoriesDisplayFragment
import com.newsta.android.utils.models.Category

const val ARG_OBJECT = "object"

class ViewPagerAdapter(private val fragmentManager: FragmentManager, val lifecycle: Lifecycle ) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val categories : ArrayList<Category> = arrayListOf()

    override fun getItemCount(): Int = categories.size

    override fun containsItem(itemId: Long): Boolean {
        return categories.contains(Category("he", itemId.toInt()))
    }

    override fun getItemId(position: Int): Long {
        return categories[position].categoryId.toLong()
    }

    override fun createFragment(position: Int): Fragment {
        println("CREATING FRAGMENTS, pos -: $position id -: ${categories[position].categoryId}")
        val fragment: Fragment = StoriesDisplayFragment()
        fragment.arguments = Bundle().apply {
            println("CATEGORY ID ---> ${categories[position].categoryId}")
            putInt(ARG_OBJECT, categories[position].categoryId)
        }
        return fragment
    }

    fun setCategories(list: List<Category>){

            categories.clear()
            categories.addAll(list)
            println("aya hai $categories")

            notifyDataSetChanged()


    }


    fun removeAll() {
        categories.removeAll(categories)
        notifyDataSetChanged()
    }

}
