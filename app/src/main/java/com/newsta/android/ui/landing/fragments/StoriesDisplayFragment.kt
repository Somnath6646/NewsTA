package com.newsta.android.ui.landing.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity.Companion._categoryLiveData
import com.newsta.android.MainActivity.Companion.categoryMutableLiveData
import com.newsta.android.MainActivity.Companion.extras
import com.newsta.android.MainActivity.Companion.maxStory
import com.newsta.android.R
import com.newsta.android.databinding.FragmentStoriesDisplayBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.ARG_OBJECT
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.MaxStoryAndUpdateTime
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel

class StoriesDisplayFragment : BaseFragment<FragmentStoriesDisplayBinding>(), OnDataSetChangedListener {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: NewsAdapter

    private fun setUpAdapter() {

        adapter = NewsAdapter { position: Int, stories: List<Story> -> openDetails(position)
        this.onDataSetChange(stories)}
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        adapter.setDataSetChangeListener(this)

    }

    private fun openDetails(position: Int) {
        val data = DetailsPageData(position)
        val bundle = bundleOf("data" to data)
        findNavController().navigate(R.id.action_landingFragment_to_detailsFragment, bundle)
    }

    private fun initViews() {

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
            }
        }

    }

    private fun observer() {

        /*viewModel.categoryState.observe(requireActivity(), Observer { state ->
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

        /*viewModel.newsDataState.observe(requireActivity(), Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")

                    viewModel.debugToast("newsDataState:  success")

                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)

                    stories.addAll(0, ArrayList(it.data))


                    val filteredStories =
                        stories.filter { story: Story -> story.category == categoryState }


                    if (filteredStories.isNullOrEmpty()) {
                        println("FILTERED STORIES MIN GHUS GAYA")
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
                    Log.i("newsDataState", "errror ${it.exception}")

                    viewModel.debugToast("newsDataState:  errror ${it.exception}")
                    binding.refreshLayout.isRefreshing = false
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
                    viewModel.debugToast("newsDataState:  loading.....")
                    binding.refreshLayout.isRefreshing = true
                }
                is DataState.Extra -> {
                    try {
                        if (it.data != null) {
                            maxStory = it.data
                            extras = arrayListOf(it.data)
                        }
                    } catch (e: Exception) {
                        viewModel.debugToast("Min Max error")
//                        Toast.makeText(requireContext(), "Min Max error", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} "
                    )
                }
            }
        })*/

        /*viewModel.dbNewsDataState.observe(requireActivity(), Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("DBnewsDataState", " success")
                    viewModel.debugToast("dBnewsDataState: success")
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
                    adapter.addAll(stories)
                }
                is DataState.Error -> {
                    Log.i("dBnewsDataState", " errror ${it.exception}")
                    viewModel.debugToast("dBnewsDataState:  errror ${it.exception}")

                    binding.refreshLayout.isRefreshing = false
                }
                is DataState.Loading -> {
                    Log.i("dBnewsDataState", " loding")
                    viewModel.debugToast("dBnewsDataState: loading")

                    binding.refreshLayout.isRefreshing = true
                }
                is DataState.Extra -> {
                    try {
                        println("EXTRA DB DATA ---> ${it.data}")
                        if (it.data != null) {
                            maxStory = it.data
                            extras = arrayListOf(it.data)
                            Log.i(
                                "newsDataState",
                                " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                            )
                            if(!isRefreshedByDefault) {
                                isRefreshedByDefault = true
                                viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                            }
                        }
                    } catch (e: Exception) {
                        viewModel.debugToast("Min max error")
                        e.printStackTrace()
                    }
                }
            }
        })*/

        /*viewModel.dbNewsLiveData.observe(requireActivity(), Observer {
            if(!it.isNullOrEmpty()) {
                binding.refreshLayout.isRefreshing = false
                viewModel.changeDatabaseState(isDatabaseEmpty = false)
                println("STORIES BEFORE ADDING NEW STORIES ---> $stories")
                stories.addAll(0, ArrayList(it))
                println("STORIES AFTER ADDING NEW STORIES ---> $stories")
                val filteredStories =
                    stories.filter { story: Story -> story.category == categoryState }
                if (filteredStories.isNullOrEmpty()) {
                    println("FILTERED STORIES MIN GHUS GAYA")
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
        })*/

        /*viewModel.newsUpdateState.observeForever( Observer {

            when (it) {
                is DataState.Success<List<Story>?> -> {
                    val updatedStories = it.data
                    val allStories = stories.toMutableList()
                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    if(updatedStories!=null){
                    updatedStories.forEach {
                        val indexOfUpdatedStory = allStories.toList().getIndexByStoryId(it.storyId)
                        if(indexOfUpdatedStory > -1){
                            println("indexwala $indexOfUpdatedStory")
                            allStories.set(indexOfUpdatedStory, it)
                        println("indexwala ${allStories[indexOfUpdatedStory].events[0].title}")
                        }

                    }
                        if(allStories.isNotEmpty())
                        stories = allStories as ArrayList<Story>
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

                        println("indexwala $stories")
                        adapter.addAll(stories)
                    }
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
                }
                is DataState.Loading -> {
                    Log.i("newsUpdateDataState", " loding")
                    viewModel.debugToast("newsUpdateDataState: loading")
                }
                is DataState.Extra -> {
                    try {
                        println("EXTRA DB DATA ---> ${it.data}")
                        if (it.data != null)
                            maxStory = it.data
                        extras = arrayListOf(it.data)
                        Log.i(
                            "newsDataState",
                            " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                        )
                        if (!isRefreshedByDefault) {
                            isRefreshedByDefault = true
                            viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                        }
                    } catch (e: Exception) {
                        viewModel.debugToast("Min max error")
                        e.printStackTrace()
                    }
                }

            }
        })*/

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
            _categoryLiveData.observe(requireActivity(), Observer { categoryState ->

                println("21466 Filter b4 categ state: $categoryState")
                val filteredStories = storiesMap[categoryState]
                if(!filteredStories.isNullOrEmpty()){
                    val stories = ArrayList<Story>(filteredStories?.toMutableList())
                    adapter.addAll(stories)
                }

            })
        })

    }

    override fun onResume() {
        super.onResume()
        println("21466 ovvver onResume catstate ${categoryMutableLiveData.value}")
        getCategoryStories()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        println("21466 ovvver onViewStateRestored ${categoryMutableLiveData.value}")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("21466 ovvver onDestroy ${categoryMutableLiveData.value}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("21466 ovvver onViewCreated ${categoryMutableLiveData.value}")

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("21466 ovvver onActivityCreated ${_categoryLiveData.value}")

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        setUpAdapter()

        Log.i("CREATING FRAGMENTS", "onActivityCreated: aya hai ")
        println("CREATING FRAGMENTS ARGUMENTS: $arguments")

        arguments?.takeIf {

            it.containsKey(ARG_OBJECT) }?.apply {
            println("ARGUMENTS --- $arguments")

            val state = getInt(ARG_OBJECT)


            println("21466 : state $state")
            val filteredStories = viewModel.storiesLiveData.value?.get(categoryMutableLiveData.value)
            if(!filteredStories.isNullOrEmpty()){
              val stories = ArrayList<Story>(filteredStories?.toMutableList())
              adapter.addAll(stories)
            }

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
