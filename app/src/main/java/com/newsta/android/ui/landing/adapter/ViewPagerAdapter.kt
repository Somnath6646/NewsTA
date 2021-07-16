package com.newsta.android.ui.landing.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.landing.fragments.StoriesDisplayFragment
import com.newsta.android.utils.models.Category

const val ARG_OBJECT = "object"

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity, private val itemCount: Int, private val categories: ArrayList<Category>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        println("CREATING FRAGMENTS")
        val fragment: Fragment = StoriesDisplayFragment()
        fragment.arguments = Bundle().apply {
            println("CATEGORY ID ---> ${categories[position].categoryId}")
            putInt(ARG_OBJECT, categories[position].categoryId)
        }
        return fragment
    }

    fun removeAll() {
        categories.removeAll(categories)
        notifyDataSetChanged()
    }

}
