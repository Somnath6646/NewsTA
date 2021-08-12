package com.newsta.android.ui.details.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailSlidingBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.adapter.NewsSourceAdapter
import com.newsta.android.ui.details.adapter.NewsSourceIconsAdapter
import com.newsta.android.ui.details.adapter.TimelineAdapter
import com.newsta.android.ui.landing.adapter.ARG_OBJECT
import com.newsta.android.utils.models.*
import com.newsta.android.viewmodels.NewsViewModel
import com.squareup.picasso.Picasso


class DetailSlidingFragment : BaseFragment<FragmentDetailSlidingBinding>() {


    private lateinit var story: Story
    private lateinit var data: DetailSlidingPageData
    private var scrollState: Int = 0
    private lateinit var event: Event

    private var isFullTimelineEnabled = MutableLiveData<Boolean>(false)

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var sourcesAdapter: NewsSourceAdapter
    private lateinit var sourcesIconsAdapter: NewsSourceIconsAdapter
    private var isSourceViewIcons = true
    private var sourcesResponse: ArrayList<NewsSource>? = null

    override fun getFragmentView(): Int = R.layout.fragment_detail_sliding

    private fun initViews() {

        event = story.events.singleOrNull {
            it.eventId == data.eventId
        }!!

        println("Story ---> ${story.storyId} \nEvents size ---> ${story.events.size} \nEvents ---> ${story.events}")
        println("Story: ${story.storyId} Event: ${event.eventId}")

        showEventData(event)

        binding.updatedAtEvent.text = NewstaApp.setTime(story.updatedAt)

//        binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = false
        setUpAdapters()

        if (story.events.size > 1) {
            binding.textTimline.visibility = View.VISIBLE
            binding.recyclerViewTimelineEvents.visibility = View.VISIBLE

            addTopToBottomOfSourceContainer()

        } else {
            binding.textTimline.visibility = View.GONE
            binding.recyclerViewTimelineEvents.visibility = View.GONE
            removeTopToBottomOfSourceContainer()
        }

        if (!MainActivity.isConnectedToNetwork) {
            binding.sourcesContainer.visibility = View.INVISIBLE
        } else {
            binding.sourcesContainer.visibility = View.VISIBLE
        }

//        adjustExtraSpace()

        binding.btnSeemoreSources.setOnClickListener {
            changeSourcesView()
            adjustExtraSpace()
            removeConstrainBottomOfSourcesToBottomOfParent()
            addTopToBottomOfSourceContainer()
        }

    }

    private fun adjustExtraSpace() {

        val scrollView = binding.scrollView

        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val childHeight = binding.constraintLayout.height
            val isScrollable =
                scrollView.height < childHeight + scrollView.paddingTop + scrollView.paddingBottom
        }

    }

    private fun addTopToBottomOfSourceContainer() {
        val layout = binding.constraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        constraintSet.connect(
            binding.sourcesContainer.id,
            ConstraintSet.TOP,
            binding.timelineContainer.id,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.applyTo(layout)
    }

    private fun removeTopToBottomOfSourceContainer() {
        val containerParams = binding.sourcesContainer.layoutParams as ConstraintLayout.LayoutParams
        containerParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
        binding.sourcesContainer.layoutParams = containerParams
    }

    private fun removeConstrainBottomOfSourcesToBottomOfParent() {
        /*val layout = binding.constraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        constraintSet.connect(binding.textSources.id, ConstraintSet.BOTTOM, binding.root.id, ConstraintSet.BOTTOM, 0)
        constraintSet.applyTo(layout)*/

        val containerParams = binding.textSources.layoutParams as ConstraintLayout.LayoutParams
        containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
        binding.textSources.layoutParams = containerParams
    }

    private fun showEventData(event: Event) {

        binding.titleEvent.text = event.title
        binding.summaryEvent.text = event.summary

        binding.updatedAtEvent.text = NewstaApp.setTime(event.createdAt)

        if(!event.imgUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(event.imgUrl)
                .into(binding.coverimgEvent)
        }

    }

    override fun onResume() {
        super.onResume()
        

    }

    private fun setUpAdapters() {

        timelineAdapter =
            TimelineAdapter { event ->
                timelineOnClick(event)
            }
        binding.recyclerViewTimelineEvents.adapter = timelineAdapter
        binding.recyclerViewTimelineEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = false

        if (story.events.size > 3) {
            val timelineAllEvents = ArrayList<Event>(story.events)

            val size = story.events.size
            val timeLineOnlyThreeEvent = arrayListOf<Event>(
                story.events[size - 3],
                story.events[size - 2],
                story.events[size - 1]
            )

            isFullTimelineEnabled.observe(viewLifecycleOwner, Observer {
                if (it) {
                    timelineAdapter =
                        TimelineAdapter { event ->
                            timelineOnClick(event)
                        }
                    binding.recyclerViewTimelineEvents.adapter = timelineAdapter
                    binding.recyclerViewTimelineEvents.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = true
                    timelineAdapter.addAll(timelineAllEvents)
                    binding.btnSeemoreTimeline.setText("See less")
                } else {
                    timelineAdapter =
                        TimelineAdapter { event ->
                            timelineOnClick(event)
                        }
                    binding.recyclerViewTimelineEvents.adapter = timelineAdapter
                    binding.recyclerViewTimelineEvents.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = false
                    timelineAdapter.addAll(timeLineOnlyThreeEvent)
                    binding.btnSeemoreTimeline.setText("See more")
                }
            })
        } else {
            val timelineAllEvents = ArrayList<Event>(story.events)
            timelineAdapter.addAll(timelineAllEvents)
            binding.btnSeemoreTimeline.visibility = View.GONE
        }

    }

    private fun changeSourcesView() {
        isSourceViewIcons = !isSourceViewIcons
        if (isSourceViewIcons)
            setSourcesIconsAdapter()
        else
            setSourcesAdapter()
    }

    private fun setSourcesIconsAdapter() {

        binding.recyclerViewSourceEvents.visibility = View.GONE
        binding.sourceIconsView.visibility = View.VISIBLE
        binding.textSources.visibility = View.GONE

        sourcesIconsAdapter =
            NewsSourceIconsAdapter()
        binding.recyclerViewSourceIcons.adapter = sourcesIconsAdapter
        binding.recyclerViewSourceIcons.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSourceIcons.isNestedScrollingEnabled = false



        sourcesIconsAdapter.addAll(ArrayList(sourcesResponse!!.distinct()))
        binding.btnSeemoreSources.text =
            if (sourcesResponse!!.size <= 6) "View all" else "+ ${sourcesResponse!!.size - 6} sources"

        if (sourcesResponse!!.size == 1) {
            binding.textSourcesIcon.text = "Source:"
        }

    }

    private fun setSourcesAdapter() {

        binding.recyclerViewSourceEvents.visibility = View.VISIBLE
        binding.sourceIconsView.visibility = View.GONE
        binding.textSources.visibility = View.VISIBLE

        sourcesAdapter =
            NewsSourceAdapter { source ->
                openNewsUrl(source)
            }
        binding.recyclerViewSourceEvents.adapter = sourcesAdapter
        if (!sourcesResponse.isNullOrEmpty()) {
            sourcesAdapter.addAll(sourcesResponse!!)
        }
        binding.recyclerViewSourceEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSourceEvents.isNestedScrollingEnabled = false

    }

    private fun openNewsUrl(source: NewsSource) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        startActivity(intent)

    }

    private fun timelineOnClick(timelineEvent: Event) {

        showEventData(timelineEvent)
        event = timelineEvent
        binding.scrollView.smoothScrollTo(0, 0)

    }


    private fun observer() {

        viewModel.sourcesDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    sourcesResponse = (it.data) as ArrayList
                    if (!sourcesResponse.isNullOrEmpty()) {
                        setSourcesIconsAdapter()
                    }
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: load horha hai sources ")

                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: error h bhai sources me")
                }
            }
        })


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        arguments?.takeIf {
            it.containsKey(ARG_OBJECT)
        }?.apply {
            data = getBundle(ARG_OBJECT)?.getParcelable<DetailSlidingPageData>(ARG_OBJECT)!!
            story = data.story
        }




        scrollState = requireArguments().getInt("scroll")

        binding.lifecycleOwner = requireActivity()


        binding.btnSeemoreTimeline.setOnClickListener {
            if (isFullTimelineEnabled.value != null) {
                isFullTimelineEnabled.value = !isFullTimelineEnabled.value!!
            }

        }

        println("VIEWMODEL: $viewModel")
        

        initViews()
        observer()

    }

}