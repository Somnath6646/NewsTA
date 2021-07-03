package com.newsta.android.ui.search.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSearchBinding
import com.newsta.android.responses.SearchStory
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.search.adapter.SearchAdapter
import com.newsta.android.utils.ShareUtil
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.Story

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel by activityViewModels<NewsViewModel>()
    private lateinit var adapter: SearchAdapter
    private var selectedEventID: Int = 0

    override fun getFragmentView(): Int  = R.layout.fragment_search

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dataToSearch = arguments?.getString("dataToSearch")
        println("ARGUMENTS $arguments")
        println("DATA TO SEARCH: $dataToSearch")
        if(!dataToSearch.isNullOrEmpty()) {
            viewModel.searchTerm.value = dataToSearch
            viewModel.getSearchResults()
        }

        if(activity?.intent?.action == Intent.ACTION_SEND) {
            binding.back.setOnClickListener {
                requireActivity().finish()
            }
        } else {
            binding.back.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        setUpAdapter()

        binding.search.setOnClickListener {
            viewModel.getSearchResults()
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
                        it.data?.first()?.let { it1 -> navigateToDetailFragment(it1, selectedEventID) }
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

//        ShareUtil.publishMemeShareShortcuts(requireContext())

    }

    private fun setUpAdapter(){
        adapter =
            SearchAdapter { story: SearchStory, eventId: Int ->
                openDetails(
                    story,
                    eventId
                )
            }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun openDetails(story: SearchStory, eventId: Int){
        this.selectedEventID = eventId
        val storyID = story.story_id
        viewModel.getStoryByIDFromSearch(storyID)
    }

    private fun navigateToDetailFragment(story: Story, eventId: Int){
        val data = DetailsPageData(story, eventId)
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(data)
        findNavController().navigate(action)
    }

}