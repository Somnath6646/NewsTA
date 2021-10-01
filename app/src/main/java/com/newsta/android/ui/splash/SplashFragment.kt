package com.newsta.android.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
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

        timer = object : CountDownTimer(2000, 1000) {
            override fun onFinish() {

                if (NewstaApp.access_token != null) {
                    //navtomainfragment
                    if(activity?.intent?.action != Intent.ACTION_SEND)
                    navigateToMainFragment()
                } else {
                    //navtoauthenticationoptionsfragment
                    navigateToAuthenticationOptionsFragment()
                }
//                navigateToAuthenticationOptionsFragment()
            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }

    fun navigateToMainFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
    }

    fun navigateToAuthenticationOptionsFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_tutorialFragment)
    }

    override fun getFragmentView(): Int = R.layout.fragment_splash

}
