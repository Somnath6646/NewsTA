package com.newsta.android.ui.landing.fragments

import android.os.Bundle
import android.util.Log
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
import com.newsta.android.databinding.FragmentStoriesDisplayBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.Story

class StoriesDisplayFragment : BaseFragment<FragmentStoriesDisplayBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: NewsAdapter

    var categoryState = 0

    private fun setUpAdapter() {

        adapter = NewsAdapter { story: Story -> openDetails(story) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(story: Story) {
        val bundle = bundleOf("data" to story)
        findNavController().navigate(R.id.action_landingFragment_to_detailsFragment, bundle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpAdapter()

        viewModel.categoryState.observe(viewLifecycleOwner, Observer { state ->
            println("CATEGORY STATE CHANGED TO: $state")
            adapter.setCategory(state)
            categoryState = state
        })

        viewModel.newsDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    val filteredStories = it.data?.filter { story: Story -> story.category == categoryState }
                    adapter.addAll(ArrayList<Story>(filteredStories))
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                    binding.refreshLayout.isRefreshing = false
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                    binding.refreshLayout.isRefreshing = true
                }
                is DataState.Extra<List<Story>?> -> {
                    val story = it.data?.get(0)!!
                    Log.i("newsDataState", " EXTRA ${story.storyId} ${story.updatedAt} ${story.category} ${story.events}")
                    viewModel.getAllNews(story.storyId, story.updatedAt)
                }
            }
        })

    }

    override fun getFragmentView(): Int = R.layout.fragment_stories_display

}
