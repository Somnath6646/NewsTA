package com.newsta.android.ui.landing.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.landing.DetailsFragment

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 8

    override fun createFragment(position: Int): Fragment {
        return Fragment()
    }
}