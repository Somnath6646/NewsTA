package com.newsta.android.ui.recommended.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.R
import com.newsta.android.databinding.FragmentRecommendedNewsBinding
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.recommended.adapter.RecommendedStoriesAdapter
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.RecommendedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel

class RecommendedStoriesFragment: BaseFragment<FragmentRecommendedNewsBinding>(), OnDataSetChangedListener {
    override fun getFragmentView(): Int  = R.layout.fragment_recommended_news

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: RecommendedStoriesAdapter

    private fun setUpAdapter() {

        adapter = RecommendedStoriesAdapter({ position: Int-> openDetails(position) })
        adapter.setDataSetChangeListener(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(position: Int) {
        val data = DetailsPageData(position)
        val bundle = bundleOf("data" to data)
        viewModel.selectedDetailsPageData = data
        findNavController().navigate(R.id.action_mainLandingFragment_to_detailsFragment2, bundle)



    }

    private fun initViews() {

        binding.refreshLayout.setOnRefreshListener {
                viewModel.getRecommendedStories()
                binding.refreshLayout.isRefreshing = false
            }
        }

    override fun onDataSetChange(stories: List<Story>) {
        viewModel.setSelectedStoryList(stories)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        setUpAdapter()
        initViews()

    }
}