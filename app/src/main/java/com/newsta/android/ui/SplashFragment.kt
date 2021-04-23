package com.newsta.android.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSplashBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.utils.prefrences.UserPrefrences

class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        object : CountDownTimer(2000, 1000){
            override fun onFinish() {


                if(NewstaApp.getAccessToken() != null){
                    //navtomainfragment
                    navigateToMainFragment()
                }else{
                    //navtoauthenticationoptionsfragment
                    navigateToAuthenticationOptionsFragment()
                }

            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }



    fun navigateToMainFragment(){
        val action = SplashFragmentDirections.actionSplashFragmentToLandingFragment()
        findNavController().navigate(action)
    }




    fun navigateToAuthenticationOptionsFragment(){
        val action = SplashFragmentDirections.actionSplashFragmentToSignupSigninOptionsFragment()
        val extras = FragmentNavigatorExtras(binding.logo to "logoTransition")

        findNavController().navigate(action, extras)
    }

    override fun getFragmentView(): Int = R.layout.fragment_splash


}