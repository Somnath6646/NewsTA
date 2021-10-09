package com.newsta.android.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSplashBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.viewmodels.NewsViewModel


class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var timer: CountDownTimer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(isAdded){
            timer = object : CountDownTimer(2200, 1000) {
                override fun onFinish() {
                    if(isAdded){
                        if (NewstaApp.access_token != null) {
                            //navtomainfragment
                            if(activity?.intent?.action != Intent.ACTION_SEND)
                                navigateToMainFragment()
                            else
                                searchWithNewsta()
                        } else {
                            navigateToAuthenticationOptionsFragment()
                        }
                    }

//                navigateToAuthenticationOptionsFragment()
                }

                override fun onTick(millisUntilFinished: Long) {

                }

            }.start()

        }

    }

    private fun searchWithNewsta() {
        if(activity != null) {
            val intent = activity?.intent
            if (intent?.action == Intent.ACTION_SEND) {
                println("14456 SHARE IF MEIN AAYA HAI")
                println("14456 SHARE INTENT: $intent")
                if (intent.type?.startsWith("text/") == true) {
                    println("14456 SHARE INTENT: $intent")
                    intent?.getStringExtra(Intent.EXTRA_TEXT)?.let { data ->
                        println("14456 INTENT SHARE TEXT: $data")
                        val dataToSearch = bundleOf("dataToSearch" to data)
                        findNavController().navigate(
                            R.id.action_splashFragment_to_searchFragment,
                            dataToSearch
                        )
                    }
                }
            }
        }
    }

    fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
    }

    fun navigateToAuthenticationOptionsFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_tutorialFragment)
    }

    override fun getFragmentView(): Int = R.layout.fragment_splash

}
