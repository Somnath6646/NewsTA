package com.newsta.android.ui.splash

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSplashBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.viewmodel.NewsViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {


                if (NewstaApp.getAccessToken() != null) {
                    //navtomainfragment
                    viewModel.getCategories()
                    navigateToMainFragment()
                } else {
                    //navtoauthenticationoptionsfragment
                    navigateToAuthenticationOptionsFragment()
                }

            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }

    fun navigateToMainFragment() {
        val action =
            SplashFragmentDirections.actionSplashFragmentToLandingFragment()
        findNavController().navigate(action)
    }

    fun navigateToAuthenticationOptionsFragment() {
        val action =
            SplashFragmentDirections.actionSplashFragmentToSignupSigninOptionsFragment()
        val extras = FragmentNavigatorExtras(binding.logo to "logoTransition")

        findNavController().navigate(action, extras)
    }

    override fun getFragmentView(): Int = R.layout.fragment_splash


}