package com.newsta.android.ui.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.google.gson.Gson
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.utils.models2.List
import com.newsta.android.utils.models2.NewsItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_landing.view.*
import org.json.JSONObject
import java.lang.StringBuilder

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by viewModels()

    private lateinit var adapter: NewsAdapter

    private fun setUpAdapter() {

        adapter = NewsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun getListFromString(stringJson: String): List {

        val stringList = "{\"list\": $stringJson}"

        val gson = Gson()

        /*val jsonObject = gson.toJson(stringList)

        println("JSON OBJECT: $jsonObject")*/

        /*val j2 = JSONObject(stringList)

        println("JSON OBJECT 2: $j2")*/

        val stList = StringBuilder(stringList)

        val list = Klaxon().parse<List>(stringList)

        //val list = Gson().fromJson(jsonObject, List::class.java)

        println("LIST: $list")

        return list!!

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
                            //adapter.addAll(getListFromString(it.data.data[0].list).list)
                            //println("News: ${it.data.data}")
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
