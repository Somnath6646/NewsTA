package com.newsta.android.ui.landing.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity.Companion.extras
import com.newsta.android.MainActivity.Companion.maxStory
import com.newsta.android.MainActivity.Companion.minStory
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentStoriesDisplayBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.ARG_OBJECT
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.Story
import java.lang.Exception

class StoriesDisplayFragment : BaseFragment<FragmentStoriesDisplayBinding>() {



    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: NewsAdapter

    var categoryState = 0
    private var stories = ArrayList<Story>()

    private fun setUpAdapter() {

        adapter = NewsAdapter { story: Story, eventId: Int -> openDetails(story, eventId) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(story: Story, eventId: Int) {
        val data = DetailsPageData(story, eventId)
        val bundle = bundleOf("data" to data)
        findNavController().navigate(R.id.action_landingFragment_to_detailsFragment, bundle)
    }

    private fun initViews() {

        viewModel.getMaxAndMinStory()

        binding.refreshLayout.setOnRefreshListener {
            if (maxStory != null) {
                viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt, true)
                viewModel.updateNews(maxStory.storyId, maxStory.updatedAt)
            } else if (!extras.isNullOrEmpty()) {
                viewModel.getAllNews(extras.first().storyId, extras.first().updatedAt)
                viewModel.updateNews(extras.first().storyId, extras.first().updatedAt)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Cannot refresh now. Try re-launching the app.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.refreshLayout.isRefreshing = false
                viewModel.getMaxAndMinStory()
            }
        }

    }


    private fun observer() {

        /*viewModel.categoryState.observe(viewLifecycleOwner, Observer { state ->
            println("CATEGORY STATE CHANGED TO: $state")
            adapter.setCategory(state)
            categoryState = state
            val filteredStories = stories.filter { story: Story -> story.category == state }
            println("FilteredStories  $filteredStories")

            val stories = ArrayList<Story>(filteredStories)
            stories.sortByDescending {
                    story ->  story.updatedAt
            }
            adapter.addAll(stories)
            viewModel.getMaxAndMinStory()
        })*/

        viewModel.newsDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    stories = ArrayList(it.data)
                    val filteredStories =
                        stories.filter { story: Story -> story.category == categoryState }
                    if (filteredStories.isNullOrEmpty()) {
                        NewstaApp.is_database_empty = true
                        viewModel.changeDatabaseState(true)
                        viewModel.getNewsOnInit()
                        NewstaApp.setIsDatabaseEmpty(true)
                    }
                    println("FilteredStories  $filteredStories")

                    val stories = ArrayList<Story>(filteredStories)
                    stories.sortByDescending {
                            story ->  story.updatedAt
                    }
                    adapter.addAll(stories)
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
                    try {
                        if (!it.data.isNullOrEmpty()) {
                            maxStory = it.data.first()
                            minStory = it.data.last()
                            extras = ArrayList(it.data)
                        }
                    } catch (e: Exception) {
//                        Toast.makeText(requireContext(), "Min Max error", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}"
                    )
                    Log.i(
                        "newsDataState",
                        " EXTRA MIN ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}"
                    )
                    viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                }
            }
        })

        viewModel.newsUpdateState.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    stories = ArrayList(it.data)
                    val filteredStories =
                        stories.filter { story: Story -> story.category == categoryState }
                    val stories = ArrayList<Story>(filteredStories)
                    println("FilteredStories Updated  $stories")
                    stories.sortByDescending {
                            story ->  story.updatedAt
                    }
                    adapter.refreshAdd(stories)
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                }
                is DataState.Extra<List<Story>?> -> {
                    try {
                        if (!it.data.isNullOrEmpty()) {
                            maxStory = it.data.first()
                            minStory = it.data.last()
                            extras = ArrayList(it.data)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Min Max error", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}"
                    )
                    Log.i(
                        "newsDataState",
                        " EXTRA MIN ${minStory.storyId} ${minStory.updatedAt} ${minStory.category} ${minStory.events}"
                    )
                    viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                }
            }

        })

        viewModel.minMaxStoryState.observe(requireActivity(), Observer {

            when (it) {
                is DataState.Success<List<Story>?> -> {
                    if (!it.data.isNullOrEmpty()) {
                        maxStory = it.data.first()
                        minStory = it.data.last()
                        extras = ArrayList(it.data)
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}"
                    )
                    Log.i(
                        "newsDataState",
                        " EXTRA MIN ${minStory.storyId} ${minStory.updatedAt} ${minStory.category} ${minStory.events}"
                    )
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                }
                is DataState.Extra<List<Story>?> -> {
                    if (!it.data.isNullOrEmpty()) {
                        maxStory = it.data.first()
                        minStory = it.data.last()
                        extras = ArrayList(it.data)
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} ${maxStory.category} ${maxStory.events}"
                    )
                    Log.i(
                        "newsDataState",
                        " EXTRA MIN ${minStory.storyId} ${minStory.updatedAt} ${minStory.category} ${minStory.events}"
                    )
                }
            }

        })

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpAdapter()

        arguments?.takeIf {
            it.containsKey(ARG_OBJECT) }?.apply {

        val  state = getInt(ARG_OBJECT)

        categoryState = state

        val filteredStories = stories.filter { story: Story -> story.category == state }
        println("FilteredStories  $filteredStories")

        val stories = ArrayList<Story>(filteredStories)
        stories.sortByDescending {
                story ->  story.updatedAt
        }
        adapter.clear()
        adapter.addAll(stories)
        viewModel.getMaxAndMinStory()

        }


        observer()
        initViews()

    }

    override fun getFragmentView(): Int = R.layout.fragment_stories_display

}
