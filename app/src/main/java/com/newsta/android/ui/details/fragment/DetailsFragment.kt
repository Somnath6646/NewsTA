package com.newsta.android.ui.details.fragment

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.drawToBitmap
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.newsta.android.MainActivity.Companion.isConnectedToNetwork
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.adapter.NewsSourceAdapter
import com.newsta.android.ui.details.adapter.NewsSourceIconsAdapter
import com.newsta.android.ui.details.adapter.TimelineAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import com.newsta.android.utils.models.*

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var story: Story
    private lateinit var data: DetailsPageData
    private var scrollState: Int = 0
    private lateinit var event: Event

    private var isFullTimelineEnabled = MutableLiveData<Boolean>(false)

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var sourcesAdapter: NewsSourceAdapter
    private lateinit var sourcesIconsAdapter: NewsSourceIconsAdapter
    private var isSourceViewIcons = true
    private var sourcesResponse: ArrayList<NewsSource>? = null

    private fun initViews() {

        event = story.events.singleOrNull {
            it.eventId == data.eventId
        }!!

        println("Story: ${story.storyId} Event: ${event.eventId}")

        showEventData(event)

        binding.updatedAtEvent.text = NewstaApp.setTime(story.updatedAt)

        setUpAdapters()

        if (story.events.size > 1) {
            binding.textTimline.visibility = View.VISIBLE
            binding.recyclerViewTimelineEvents.visibility = View.VISIBLE

            addTopToBottomOfSourceContainer()


        } else {
            binding.textTimline.visibility = View.GONE
            binding.recyclerViewTimelineEvents.visibility = View.GONE
            binding.extraSpace.visibility = View.VISIBLE
            removeTopToBottomOfSourceContainer()
        }

        viewModel.getSavedStory(story.storyId)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnShare.setOnClickListener { shareImage() }

        binding.btnDownload.setOnClickListener { saveStory() }

        binding.btnDownloaded.setOnClickListener { deleteSavedStory() }

        if (!isConnectedToNetwork) {
            binding.sourcesContainer.visibility = View.INVISIBLE
        } else {
            binding.sourcesContainer.visibility = View.VISIBLE
        }

//        adjustExtraSpace()

        binding.btnSeemoreSources.setOnClickListener { changeSourcesView()
            adjustExtraSpace()
            removeConstrainBottomOfSourcesToBottomOfParent()
            addTopToBottomOfSourceContainer()
        }

    }

    private fun adjustExtraSpace(){

        val scrollView = binding.scrollView

        scrollView.viewTreeObserver.addOnGlobalLayoutListener{
            val childHeight = binding.constraintLayout.height
            val isScrollable = scrollView.height < childHeight + scrollView.paddingTop + scrollView.paddingBottom
            if(isScrollable){
                binding.extraSpace.visibility = View.VISIBLE
            }else{
                binding.extraSpace.visibility = View.GONE
            }
        }




    }

    private  fun addTopToBottomOfSourceContainer(){
        val layout = binding.constraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        constraintSet.connect(binding.sourcesContainer.id, ConstraintSet.TOP, binding.timelineContainer.id, ConstraintSet.BOTTOM, 0)
        constraintSet.applyTo(layout)
    }

    private  fun removeTopToBottomOfSourceContainer(){
        val containerParams = binding.sourcesContainer.layoutParams as ConstraintLayout.LayoutParams
        containerParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
        binding.sourcesContainer.layoutParams = containerParams
    }

    private fun removeConstrainBottomOfSourcesToBottomOfParent(){
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

        viewModel.getSources(story.storyId, event.eventId)

        binding.titleEvent.text = event.title

        binding.summaryEvent.text = event.summary

        binding.updatedAtEvent.text = NewstaApp.setTime(event.createdAt)

        Picasso.get()
            .load(event.imgUrl)
            .into(binding.coverimgEvent)

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
        if(isSourceViewIcons)
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
        binding.recyclerViewSourceIcons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSourceIcons.isNestedScrollingEnabled = false

        sourcesIconsAdapter.addAll(sourcesResponse!!)
        binding.btnSeemoreSources.text = if(sourcesResponse!!.size <= 6) "View all" else "+ ${sourcesResponse!!.size - 6} sources"

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
        if(!sourcesResponse.isNullOrEmpty()) {
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

    private fun saveStory() {

        println("SAVING STORY")

        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events
        )

        viewModel.saveStory(savedStory)
        Snackbar.make(binding.root, "News story saved", Snackbar.LENGTH_SHORT).show()
        binding.btnDownload.visibility = View.INVISIBLE

    }

    private fun deleteSavedStory() {

        println("DELETING SAVEDSTORY")

        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events
        )

        viewModel.deleteSavedStory(savedStory)
        binding.btnDownload.visibility = View.VISIBLE
        Snackbar.make(binding.root, "News story unsaved", Snackbar.LENGTH_SHORT).show()
    }

    private fun showDeleteDialog(savedStory: SavedStory) {

        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.logout_dialog,
            null,
            false
        )
        dialog.setContentView(dialogBinding.root)

        dialogBinding.message.text = "Are you sure you want to delete this story?"
        dialogBinding.buttonTextAction.text = "Delete"

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnAction.setOnClickListener {
            viewModel.deleteSavedStory(savedStory)
            binding.btnDownload.visibility = View.VISIBLE
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

    }

    private fun observer() {

        viewModel.sourcesDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    sourcesResponse = (it.data) as ArrayList
                    if(!sourcesResponse.isNullOrEmpty()) {
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

        viewModel.saveNewsState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {
                    binding.btnDownload.visibility = View.INVISIBLE
                    println("NEWS SAVE SUCCESSFUL")
                }
                is DataState.Error -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVING")
                }
            }
        })

        viewModel.checkSavedStoryState.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success<SavedStory?> -> {
                    val savedStory = it.data
                    if (it.data != null) {
                        binding.btnDownload.visibility = View.INVISIBLE
                        println("NEWS WAS SAVED")
                    }
                }
                is DataState.Error -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVING")
                }
            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        data = requireArguments().getParcelable<DetailsPageData>("data")!!

        story = data.story

        scrollState = requireArguments().getInt("scroll")

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSeemoreTimeline.setOnClickListener {
            if (isFullTimelineEnabled.value != null) {
                isFullTimelineEnabled.value = !isFullTimelineEnabled.value!!
            }

        }

        println("VIEWMODEL: $viewModel")

        initViews()
        observer()


    }

    private fun shareImage() {
        val imageUri = binding.constraintLayout.drawToBitmap().let { bitmap: Bitmap ->
            saveBitmap(requireActivity(), bitmap)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)

        startActivity(Intent.createChooser(intent, event.title))
    }

    val DEFAULT_FILENAME = "${System.currentTimeMillis()}.jpeg"

    fun saveBitmap(activity: Activity, bitmap: Bitmap, filename: String = DEFAULT_FILENAME): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val contentResolver = activity.contentResolver

        val imageUri: Uri? = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        return imageUri.also {
            val fileOutputStream = imageUri?.let { contentResolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream?.close()
        }
    }

    override fun getFragmentView(): Int = R.layout.fragment_details

}
