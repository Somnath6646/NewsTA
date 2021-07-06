package com.newsta.android.ui.search.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSearchBinding
import com.newsta.android.responses.SearchStory
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.search.adapter.SearchAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.Story

class SearchFragment : BaseFragment<FragmentSearchBinding>() {


    private val viewModel by activityViewModels<NewsViewModel>()
    private lateinit var adapter: SearchAdapter
    private var selectedEventID: Int = 0
    private var position: Int = 0

    override fun getFragmentView(): Int  = R.layout.fragment_search

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        setUpAdapter()

        binding.searchLayout.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.getSearchResults()
                binding.root.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        binding.search.setOnClickListener {

            viewModel.getSearchResults()
            binding.root.clearFocus()
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.searchDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<SearchStory>?> -> {
                    println("Stories got suceessfully")
                    binding.progressBar.visibility = View.GONE
                    adapter.addAll(it.data as ArrayList<SearchStory>)
                }
                is DataState.Loading -> {
                    println("Stories getting")
                    binding.progressBar.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    println("Stories not got suceessfully ")
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.exception, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.storyByIDDataState.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                when(it){
                    is DataState.Success<List<Story>?> -> {
                        println("Story detail got suceessfully")
                        binding.progressBar.visibility = View.GONE
                        it.data?.first()?.let { it1 -> navigateToDetailFragment(it1, this.position) }
                    }
                    is DataState.Loading -> {
                        println("Story detail getting")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is DataState.Error -> {
                        println("Story detail not got suceessfully ")
                        binding.progressBar.visibility = View.GONE

                        Toast.makeText(requireContext(), it.exception, Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }

        })

    }

    private fun setUpAdapter(){
        adapter =
            SearchAdapter { story: SearchStory, eventId: Int, position: Int ->
                openDetails(
                    story,
                    eventId,
                    position
                )
            }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun openDetails(story: SearchStory, eventId: Int, position: Int){
        this.selectedEventID = eventId
        this.position = 0
        val storyID = story.story_id
        viewModel.getStoryByIDFromSearch(storyID)
    }

    private fun navigateToDetailFragment(story: Story, position: Int){
        val data = DetailsPageData(position)
        val stories = ArrayList<Story>()
        stories.add(story)
        viewModel.setSelectedStoryList(stories)
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(data)
        findNavController().navigate(action)
    }

}