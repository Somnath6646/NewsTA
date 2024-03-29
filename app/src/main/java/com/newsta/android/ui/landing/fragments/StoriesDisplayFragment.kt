package com.newsta.android.ui.landing.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity.Companion.categoryState
import com.newsta.android.MainActivity.Companion.extras
import com.newsta.android.MainActivity.Companion.maxStory
import com.newsta.android.R
import com.newsta.android.databinding.FragmentStoriesDisplayBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.DetailsActivity
import com.newsta.android.ui.landing.adapter.ARG_OBJECT
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.MaxStoryAndUpdateTime
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel
import kotlin.math.max

class StoriesDisplayFragment : BaseFragment<FragmentStoriesDisplayBinding>(), OnDataSetChangedListener {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: NewsAdapter

    private fun setUpAdapter() {

        adapter = NewsAdapter { position: Int, stories: List<Story> -> openDetails(position, stories)
        this.onDataSetChange(stories)}
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        adapter.setDataSetChangeListener(this)

    }

    private fun openDetails(position: Int, stories: List<Story>) {
        val data = DetailsPageData(position, stories[position].events.last().eventId)
        val bundle = bundleOf("data" to data)
        viewModel.selectedDetailsPageData = data
        findNavController().navigate(R.id.action_mainLandingFragment_to_detailsFragment2, bundle)
    }

    private fun initViews() {

        binding.refreshLayout.setOnRefreshListener {
            val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)

            if (maxStory != null) {
                viewModel.getNewStories(maxStory.storyId, max(days3, maxStory.updatedAt) , true)
                viewModel.updateNews(maxStory.storyId, max(days3, maxStory.updatedAt))

            } else if (!extras.isNullOrEmpty()) {
                viewModel.getNewStories(extras.first().storyId, max(extras.first().updatedAt, days3))
                viewModel.updateNews(extras.first().storyId, max(extras.first().updatedAt, days3))
            } else {
                Toast.makeText(
                    requireContext(),
                    "Cannot refresh now. Try re-launching the app.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.refreshLayout.isRefreshing = false
            }
        }

    }

    private fun observer() {

        viewModel.minMaxStoryState.observe(requireActivity(), Observer {

            when (it) {
                is DataState.Success<MaxStoryAndUpdateTime>-> {
                    if (it.data != null) {
                        maxStory = it.data
                        extras = arrayListOf(it.data)
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                    )
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                }
                is DataState.Extra -> {
                    if (it.data != null) {
                        maxStory = it.data
                        extras = arrayListOf(it.data)
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                    )
                }
            }

        })

    }

    fun getCategoryStories() {

        viewModel.storiesLiveData.observe(requireActivity(), Observer { storiesMap ->
                val filteredStories = storiesMap[categoryState]
                if(!filteredStories.isNullOrEmpty()){
                    val stories = ArrayList<Story>(filteredStories?.toMutableList())
                    adapter.addAll(stories)
                }
        })

    }

    override fun onResume() {
        super.onResume()
        getCategoryStories()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        setUpAdapter()


        arguments?.takeIf {

            it.containsKey(ARG_OBJECT) }?.apply {

            val state = getInt(ARG_OBJECT)

        }

        observer()
        viewModel.getMaxAndMinStory()

        initViews()

    }

    override fun getFragmentView(): Int = R.layout.fragment_stories_display

    override fun onDataSetChange(stories: List<Story>) {
        viewModel.setSelectedStoryList(stories)
    }

}
