package com.newsta.android.ui.landing

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.landing.adapter.ViewPagerAdapter
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


        binding.sideNavDrawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.log_out -> {
                    val dialog = Dialog(requireContext())
                    val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(LayoutInflater.from(requireContext()), R.layout.logout_dialog, null, false)
                    dialog.setContentView(dialogBinding.root)

                    println("Abhi hai $dialogBinding")


                    dialogBinding.btnCancel.setOnClickListener { v ->
                        dialog.dismiss()
                    }

                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                    dialog.show()
                    true
                }
                else -> {
                    false
                }
            }
        }

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

        binding.pager.adapter = createPagerAdapter()
        setUpTabLayout()

    }



    private fun setUpTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.pager,
                TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                    tab.customView = when (position) {
                        0 -> addCustomView(
                                "All News", 16f,
                                Color.WHITE
                        )
                        1 -> addCustomView("General")
                        2 -> addCustomView("Technology")
                        3 -> addCustomView("Business")
                        4 -> addCustomView("Health")
                        5 -> addCustomView("Science")
                        6 -> addCustomView("Entertainment")
                        7 -> addCustomView("Sports")
                        else -> addCustomView("null")
                    }


                }).attach()

        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

                tab?.view?.children?.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorSecondary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.setTextSize(14f)
                                view2.setTextColor(resources.getColor(R.color.colorText))
                            }
                        }
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {


                tab?.view?.children?.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.setTextSize(16f)
                                view2.setTextColor(Color.WHITE)
                            }
                        }
                    }
                }

            }

        })

    }


    fun addCustomView(
            title: String,
            size: Float = 14f,
            color: Int = R.color.colorText
    ): View {
        val view = layoutInflater.inflate(R.layout.item_tab, binding.tabLayout, false)
        val titleTextView = view.findViewById<TextView>(R.id.title)
        titleTextView.apply {
            text = title


            if (color == Color.WHITE) {
                (view.findViewById<CardView>(R.id.cardView)).setCardBackgroundColor(
                        resources.getColor(
                                R.color.colorPrimary
                        )
                )
                setTextColor(color)
            } else {
                setTextColor(resources.getColor(color))
            }

            textSize = size
        }

        return view
    }



    private fun createPagerAdapter(): ViewPagerAdapter =
            ViewPagerAdapter(requireActivity())


    override fun getFragmentView(): Int  = R.layout.fragment_landing

}
