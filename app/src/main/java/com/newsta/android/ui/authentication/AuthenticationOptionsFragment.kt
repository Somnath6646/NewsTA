package com.newsta.android.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.ui.authentication.AuthenticationOptionsFragment
import com.newsta.android.databinding.FragmentSignupSigninOptionsBinding


class AuthenticationOptionsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentSignupSigninOptionsBinding>(inflater,
            R.layout.fragment_signup_signin_options, container, false )

        binding.btnSignup3.setOnClickListener {
            val action =
                AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.btnSignin.setOnClickListener {
            val action = AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToSigninFragment()
            findNavController().navigate(action)
        }

        return binding.root

    }


}