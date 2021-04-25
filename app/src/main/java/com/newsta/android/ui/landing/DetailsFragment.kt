package com.newsta.android.ui.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.TimelineAdapter

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.recyclerViewTimelineEvents.adapter = TimelineAdapter()
        binding.recyclerViewTimelineEvents.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun getFragmentView(): Int = R.layout.fragment_details

}