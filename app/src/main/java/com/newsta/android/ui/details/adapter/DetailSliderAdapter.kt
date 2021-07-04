package com.newsta.android.ui.details.adapter


import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newsta.android.ui.details.fragment.DetailSlidingFragment
import com.newsta.android.ui.landing.fragments.StoriesDisplayFragment
import com.newsta.android.utils.models.Category
import com.newsta.android.utils.models.DetailSlidingPageData
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.Story

const val ARG_OBJECT = "object"

class DetailSliderAdapter(private val fragmentActivity: FragmentActivity, private val itemCount: Int, private val stories: ArrayList<Story>) : FragmentStateAdapter(fragmentActivity) {



    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = DetailSlidingFragment()
        fragment.arguments = Bundle().apply {

            val story = stories.get(position)

            val events = story.events.sortedByDescending { events -> events.updatedAt }

            val event = events.last()

            val data = DetailSlidingPageData(story = story , eventId = event.eventId)
            val bundle = bundleOf(ARG_OBJECT to data)
            putBundle(ARG_OBJECT, bundle)
        }
        return fragment

    }

}