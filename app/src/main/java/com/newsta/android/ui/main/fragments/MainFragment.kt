package com.newsta.android.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentMainBinding
import com.newsta.android.interfaces.DetailsBottomNavInterface
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.fragment.DetailsFragment
import com.newsta.android.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(), DetailsBottomNavInterface {

    private lateinit var navController: NavController
    private val viewModel by viewModels<NewsViewModel>()

    private fun setUpSmoothBottomMenu() {
        val popupMenu = PopupMenu(context, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        val menu = popupMenu.menu
//        binding.bottomNav.setupWithNavController(menu, navController)
        binding.bottomNav.setupWithNavController(navController)
        var badge = binding.bottomNav.getOrCreateBadge(R.id.notifyFragment)
        badge.isVisible = true
        val badgeDrawable = binding.bottomNav.getBadge(R.id.notifyFragment)
        if (badgeDrawable != null) {
            badgeDrawable.backgroundColor =
                NewstaApp.res?.let { ResourcesCompat.getColor(it, R.color.colorPrimary, null) }!!
        }
        viewModel.notifyStoriesLiveData.observe(requireActivity(), Observer {payloads ->
            var count = 0
            if(payloads !=null){
                payloads.forEach {
                    if(it.read == ArticleState.UNREAD){
                        count++
                    }
                }
                if(count <1){
                    if (badgeDrawable != null) {
                        badgeDrawable.isVisible = false
                        badgeDrawable.clearNumber()
                    }
                }else{
                    badge.number = count
                }


            }
        })



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

    override fun isBottomNavEnabled(isEnabled: Boolean) {
        if(isEnabled) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }
}
