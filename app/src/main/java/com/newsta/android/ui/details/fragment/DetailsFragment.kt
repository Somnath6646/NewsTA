package com.newsta.android.ui.details.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.adapter.NewsSourceAdapter
import com.newsta.android.ui.details.adapter.TimelineAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import com.newsta.android.utils.models.*

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var story: Story
    private var scrollState: Int = 0
    private lateinit var event: Event

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var sourcesAdapter: NewsSourceAdapter

    private fun initViews() {

        event = story.events.last()

        println("Story: ${story.storyId} Event: ${event.eventId}")

        showEventData(event)

        setUpAdapters()

        if (story.events.size > 1) {
            binding.textTimline.visibility = View.VISIBLE
            binding.recyclerViewTimelineEvents.visibility = View.VISIBLE
        } else {
            binding.textTimline.visibility = View.GONE
            binding.recyclerViewTimelineEvents.visibility = View.GONE
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnShare.setOnClickListener { shareImage() }

        binding.btnDownload.setOnClickListener { saveStory() }

    }

    private fun showEventData(event: Event) {

        viewModel.getSources(story.storyId, event.eventId)

        binding.titleEvent.text = event.title

        binding.summaryEvent.text = event.summary

        binding.updatedAtEvent.text = "${NewstaApp.setTime(story.updatedAt)}"

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

        val timelineEvents = ArrayList<Event>(story.events)

        timelineAdapter.addAll(timelineEvents)

        sourcesAdapter =
            NewsSourceAdapter { source ->
                openNewsUrl(source)
            }
        binding.recyclerViewSourceEvents.adapter = sourcesAdapter
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

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        story = requireArguments().getParcelable<Story>("data")!!
        scrollState = requireArguments().getInt("scroll")

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        println("VIEWMODEL: $viewModel")

        initViews()

        viewModel.sources.observe(viewLifecycleOwner, Observer { response ->
            val data = response.data
            sourcesAdapter.addAll(ArrayList(data))
        })

        viewModel.saveNewsState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {
                    binding.btnDownload.setImageDrawable(resources.getDrawable(R.drawable.ic_downloaded))
                    println("NEWS SAVE SUCCESSFUL")
                }
                is DataState.Error -> {
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    println("NEWS SAVING")
                }
            }
        })

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
