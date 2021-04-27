package com.newsta.android.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.TimelineAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.ui.landing.viewmodel.NewsViewModel_AssistedFactory
import com.newsta.android.utils.Event
import com.newsta.android.utils.models.Data
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var data: Data
    private var scrollState: Int = 0

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: TimelineAdapter

    private fun initViews() {

        binding.titleEvent.text = data.events.last().title

        binding.summaryEvent.text = data.events.last().summary

        setDate(data.events.last().updatedAt)

        Picasso.get()
            .load(data.events.last().imgUrl)
            .into(binding.coverimgEvent)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

    }

    private fun setUpAdapter() {

        adapter = TimelineAdapter()
        binding.recyclerViewTimelineEvents.adapter = adapter
        binding.recyclerViewTimelineEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimelineEvents.isNestedScrollingEnabled = false

        adapter.addAll(data.events as ArrayList)

    }

    private fun setDate(millis: Long) {

        val updatedAt = Calendar.getInstance()

        updatedAt.timeInMillis = millis

        val calendar = Calendar.getInstance()

        println(
            " YEAR:  ${updatedAt.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)} ${updatedAt.get(
                Calendar.YEAR)} ${calendar.get(Calendar.YEAR)}"
        )
        println(" MONTH:  ${updatedAt.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)}  ${updatedAt.get(
            Calendar.MONTH)} ${calendar.get(Calendar.MONTH)}")
        println(" DAY_OF_MONTH:  ${updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)} ${updatedAt.get(
            Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.DAY_OF_MONTH)}")

        if (updatedAt.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {

            if (updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {

                if (updatedAt.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)) {

                    println("Updated ${calendar.get(Calendar.MINUTE)} - ${updatedAt.get(Calendar.MINUTE)}")

                    val minutes =
                        Math.abs(calendar.get(Calendar.MINUTE) - updatedAt.get(Calendar.MINUTE))

                    binding.updatedAtEvent.text = "Updated $minutes minutes ago"

                } else {

                    println("Updated ${calendar.get(Calendar.HOUR_OF_DAY)} - ${updatedAt.get(Calendar.HOUR_OF_DAY)}")

                    val hours = Math.abs(
                        calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)
                    )

                    binding.updatedAtEvent.text = "Updated $hours hours ago"

                }

            } else if (updatedAt.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) - 1) {


                val hours =
                    calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)

                if (hours > 0)
                    binding.updatedAtEvent.text = "Updated $hours minutes ago"
                else {
                    binding.updatedAtEvent.text = "1 day ago"
                }

            } else {
                val days =
                    calendar.get(Calendar.DAY_OF_MONTH) - updatedAt.get(Calendar.DAY_OF_MONTH)

                binding.updatedAtEvent.text = "Updated $days days ago"
            }

        } else {

            val months =
                calendar.get(Calendar.HOUR_OF_DAY) - updatedAt.get(Calendar.HOUR_OF_DAY)

            binding.updatedAtEvent.text = "Updated $months months ago"

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        data = requireArguments().getParcelable<Data>("data")!!
        scrollState = requireArguments().getInt("scroll")

        viewModel.newsScrollState.value = scrollState

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/web"
            intent.putExtra(Intent.EXTRA_TEXT, data.events.last().title + "\n"+data.events.last().summary)
            val shareIntent = Intent.createChooser(intent, "Share via")
            startActivity(shareIntent)
        }

        initViews()
        setUpAdapter()

    }

    override fun getFragmentView(): Int = R.layout.fragment_details

}
