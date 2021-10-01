package com.newsta.android.ui.recommended.fragments

import android.os.Bundle
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.R
import com.newsta.android.databinding.FragmentRecommendedNewsBinding

class RecommendedStoriesFragment: BaseFragment<FragmentRecommendedNewsBinding>() {
    override fun getFragmentView(): Int  = R.layout.fragment_recommended_news

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}