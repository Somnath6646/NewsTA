package com.newsta.android.ui.saved.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story

class SavedStoriesFragment : BaseFragment<FragmentSavedStoriesBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: SavedStoryAdapter

    private fun setUpAdapter() {

        adapter = SavedStoryAdapter { savedStory: SavedStory -> openDetails(savedStory) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(savedStory: SavedStory) {
        val story = Story(
            category = savedStory.category,
            updatedAt = savedStory.updatedAt,
            events = savedStory.events,
            storyId = savedStory.storyId)
        val bundle = bundleOf("data" to story)
        findNavController().navigate(R.id.action_savedStoriesFragment_to_detailsFragment, bundle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpAdapter()

        viewModel.getSavedStories()

        viewModel.savedStoriesState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<SavedStory>> -> {
                    adapter.addAll(ArrayList(it.data))
                }
                is DataState.Error -> {
                    println("NEWS SAVED FETCH ERROR")
                }
                is DataState.Loading -> {
                    println("NEWS SAVED LOADING")
                }
            }
        })

        binding.back.setOnClickListener { findNavController().popBackStack() }

    }

    override fun getFragmentView(): Int = R.layout.fragment_saved_stories

}
