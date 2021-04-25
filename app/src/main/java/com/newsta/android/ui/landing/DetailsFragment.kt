package com.newsta.android.ui.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.TimelineAdapter
import com.newsta.android.utils.models.Data
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var data: Data

    private lateinit var adapter: TimelineAdapter

    private fun initViews() {

        binding.titleEvent.text = data.events[0].title

        binding.summaryEvent.text = data.events[0].summary

        setDate(data.events[0].updatedAt)

        Picasso.get()
            .load(data.events[0].imgUrl)
            .into(binding.coverimgEvent)

    }

    private fun setUpAdapter() {

        adapter = TimelineAdapter()
        binding.recyclerViewTimelineEvents.adapter = adapter
        binding.recyclerViewTimelineEvents.layoutManager = LinearLayoutManager(requireContext())

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

        initViews()
        setUpAdapter()

    }

    override fun getFragmentView(): Int = R.layout.fragment_details

}