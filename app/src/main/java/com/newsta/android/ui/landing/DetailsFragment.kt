package com.newsta.android.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.TimelineAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.utils.models.Event
import com.newsta.android.utils.models.Story
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var story: Story
    private var scrollState: Int = 0

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: TimelineAdapter

    private fun initViews() {

        binding.titleEvent.text = story.events.last().title

        binding.summaryEvent.text = story.events.last().summary

        binding.updatedAtEvent.text = "Updated ${setTime(story.events.last().updatedAt)}"

        Picasso.get()
            .load(story.events.last().imgUrl)
            .into(binding.coverimgEvent)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

    }

    private fun setUpAdapter() {

        adapter = TimelineAdapter()
        binding.recyclerViewTimelineEvents.adapter = adapter
        binding.recyclerViewTimelineEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = false

        adapter.addAll(ArrayList<Event>(story.events))

    }

    private fun setTime(updatedAt: Long): String {

        val time = System.currentTimeMillis()

        val diff = time - updatedAt

        val seconds: Long = diff / 1000

        val minutes: Int = (seconds / 60).toInt()

        val hours: Int = minutes / 60
        val days: Int = hours / 24
        val months: Int = days / 30
        val years: Int = months / 12

        if (minutes <= 30) {
            return "Few minutes ago"
        }
        else if (minutes > 30 && minutes < 60) {
            return "Less than an hour ago"
        } else {

            if (hours == 1)
                return "An hour ago"
            else if (hours > 1 && hours < 24) {
                return "$hours hours ago"
            } else {
                if (days >= 1 && days < 30) {
                    return "$days days ago"
                } else {
                    if (months >= 1 && months < 12) {
                        return "$months months ago"
                    } else {
                        if (years == 1)
                            return "An year ago"
                        else if (years > 1)
                            return "$years years ago"
                        else
                            return "Time unknown"
                    }
                }
            }

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        story = requireArguments().getParcelable<Story>("data")!!
        scrollState = requireArguments().getInt("scroll")

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/web"
            intent.putExtra(Intent.EXTRA_TEXT, story.events.last().title + "\n" +story.events.last().summary)
            val shareIntent = Intent.createChooser(intent, "Share via")
            startActivity(shareIntent)
        }

        initViews()
        setUpAdapter()

    }

    override fun getFragmentView(): Int = R.layout.fragment_details

}
