package com.newsta.android.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.newsta.android.R
import com.newsta.android.databinding.FragmentMainBinding
import com.newsta.android.interfaces.DetailsBottomNavInterface
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.fragment.DetailsFragment
import com.newsta.android.viewmodels.NewsViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(), DetailsBottomNavInterface {

    private lateinit var navController: NavController
    private val viewModel by viewModels<NewsViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })

    private fun setUpSmoothBottomMenu() {
        val popupMenu = PopupMenu(context, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        val menu = popupMenu.menu
//        binding.bottomNav.setupWithNavController(menu, navController)
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*val localNavHost = childFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment
        navController = localNavHost.navController*/
        navController = activity?.findNavController(R.id.main_nav_fragment)!!
        setUpSmoothBottomMenu()
        DetailsFragment.setDetailsBottomNavInterface(this)

//        viewModel.setDetailsBottomNavInterface(DetailsBottomNavInterface)

    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.recommendedStoriesFragment -> {
            }

            R.id.landingFragment -> {
            }

            R.id.notifyFragment -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun getFragmentView(): Int = R.layout.fragment_main

    fun toEndTransition() {
        binding.bottomNav.visibility = View.GONE
    }

    fun toStartTransition() {
        binding.bottomNav.visibility = View.VISIBLE

    }

    override fun isBottomNavEnabled(isEnabled: Boolean) {
        if(isEnabled) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }
}
