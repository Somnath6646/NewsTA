package com.newsta.android.ui.landing.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.landing.fragments.StoriesDisplayFragment

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity, private val itemCount: Int) : FragmentStateAdapter(fragmentActivity) {



    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        return StoriesDisplayFragment()
    }

}