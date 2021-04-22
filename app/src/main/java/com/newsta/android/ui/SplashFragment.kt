package com.newsta.android.ui

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSplashBinding
import com.newsta.android.ui.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        object : CountDownTimer(2000, 1000){
            override fun onFinish() {
                val action = SplashFragmentDirections.actionSplashFragmentToSignupSigninOptionsFragment()
                val extras = FragmentNavigatorExtras(binding.logo to "logoTransition")

                findNavController().navigate(action, extras)
            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }

    override fun getFragmentView(): Int = R.layout.fragment_splash


}