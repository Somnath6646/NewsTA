package com.newsta.android.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by viewModels()

    private lateinit var adapter: NewsAdapter

    private fun setUpAdapter() {

        adapter = NewsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        println("Access token: ${NewstaApp.access_token}")

        setUpAdapter()

        viewModel.getAllNews()

        viewModel.newsResponse.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled().let {

                when(it) {

                    is Resource.Success -> {
                        if (!it.data.data.isNullOrEmpty()) {
                            adapter.addAll(it.data.data)
                            println("News: ${it.data.data}")
                        } else {
                            println("News Response is NULL")
                        }
                    }

                    is Resource.Failure -> {
                        println("News Response failure $it")
                    }

                }

            }
        })

    }

    override fun getFragmentView(): Int  = R.layout.fragment_landing

}
