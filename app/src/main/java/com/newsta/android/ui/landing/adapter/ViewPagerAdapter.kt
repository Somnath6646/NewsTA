package com.newsta.android.ui.landing.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.landing.fragments.StoriesDisplayFragment

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val itemCount = 8

    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        return StoriesDisplayFragment()
    }

}