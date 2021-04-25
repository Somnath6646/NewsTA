package com.newsta.android.ui.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.utils.models.Data
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by viewModels()

    private lateinit var adapter: NewsAdapter

    private fun setUpAdapter() {

        adapter = NewsAdapter { data: Data -> openDetails(data) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun setUpNavigationDrawer() {

        binding.navDrawer.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.sideNavDrawer.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer.menu.getItem(1).subMenu.getItem(0).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    /*fun addNewsToDatabase(stories: ArrayList<Data>) {
        viewModel.viewModelScope.launch {
            if(viewModel.insertNewsToDatabase(stories) != 0L) {
                println("NEWS ADDED TO DATABASE")
            }
        }
    }*/

    private fun openDetails(data: Data) {
        val bundle = bundleOf("data" to data)
        findNavController().navigate(R.id.action_landingFragment_to_detailsFragment, bundle)
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

        setUpNavigationDrawer()

    }

    override fun getFragmentView(): Int  = R.layout.fragment_landing

}
