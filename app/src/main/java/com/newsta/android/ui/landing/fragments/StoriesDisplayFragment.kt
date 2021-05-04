package com.newsta.android.ui.landing.fragments

import android.os.Bundle
import android.util.Log
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
    private var stories = ArrayList<Story>()
    private lateinit var maxStory: Story
    private lateinit var minStory: Story

    private fun setUpAdapter() {

        adapter = NewsAdapter { story: Story -> openDetails(story) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(story: Story) {
        val bundle = bundleOf("data" to story)
        findNavController().navigate(R.id.action_landingFragment_to_detailsFragment, bundle)
    }

    private fun initViews() {

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
            viewModel.updateNews(maxStory.storyId, maxStory.updatedAt)
        }
        viewModel.getMaxAndMinStory()
        println("FRAGMENT INITIALIZED")

    }

    private fun observer() {

        viewModel.categoryState.observe(viewLifecycleOwner, Observer { state ->
            println("CATEGORY STATE CHANGED TO: $state")
            adapter.setCategory(state)
            categoryState = state
            val filteredStories = stories.filter { story: Story -> story.category == state }
            println("FilteredStories  $filteredStories")
            adapter.addAll(ArrayList<Story>(filteredStories))
            viewModel.getMaxAndMinStory()
        })

        viewModel.newsDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    stories = ArrayList(it.data!!)
                    val filteredStories = stories.filter { story: Story -> story.category == categoryState }
                    println("FilteredStories  $filteredStories")
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
                    maxStory = it.data?.first()!!
                    minStory = it.data.last()!!
                    Log.i("newsDataState", " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}")
                    Log.i("newsDataState", " EXTRA MIN ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}")
                    viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                }
            }
        })

        viewModel.newsUpdateState.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    stories = ArrayList(it.data!!)
                    val filteredStories = stories.filter { story: Story -> story.category == categoryState }
                    println("FilteredStories  $filteredStories")
                    adapter.addAll(ArrayList<Story>(filteredStories))
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                }
                is DataState.Extra<List<Story>?> -> {
                    maxStory = it.data?.first()!!
                    minStory = it.data.last()
                    Log.i("newsDataState", " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}")
                    Log.i("newsDataState", " EXTRA MIN ${minStory.storyId} ${minStory.updatedAt} ${minStory.category} ${minStory.events}")
                    viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                }
            }

        })

        viewModel.minMaxStoryState.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success<List<Story>?> -> {
                    maxStory = it.data?.first()!!
                    minStory = it.data.last()
                    Log.i("newsDataState", " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}")
                    Log.i("newsDataState", " EXTRA MIN ${minStory.storyId} ${minStory.updatedAt} ${minStory.category} ${minStory.events}")
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                }
                is DataState.Extra<List<Story>?> -> {}
            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpAdapter()
        observer()
        initViews()

    }

    override fun getFragmentView(): Int = R.layout.fragment_stories_display

}
