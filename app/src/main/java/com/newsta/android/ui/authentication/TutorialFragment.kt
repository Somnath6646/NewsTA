package com.newsta.android.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.newsta.android.R
import com.newsta.android.databinding.FragmentTutorialBinding
import com.newsta.android.ui.authentication.adapter.TutorialAdapter
import com.newsta.android.ui.base.BaseFragment

class TutorialFragment : BaseFragment<FragmentTutorialBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = TutorialAdapter()
        binding.pager.adapter = adapter
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.setViewPager2(binding.pager)

    }

    override fun getFragmentView(): Int = R.layout.fragment_tutorial

}
