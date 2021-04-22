package com.newsta.android.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSignupSigninOptionsBinding
import java.util.*


class AuthenticationOptionsFragment : Fragment() {


    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentSignupSigninOptionsBinding>(inflater,
            R.layout.fragment_signup_signin_options, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        ViewCompat.setTransitionName(binding.imageView, "logoTransition")

        binding.btnSignup3.setOnClickListener {
            val action =
                AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToEmailSignUpFragment()
            findNavController().navigate(action)
        }

        binding.btnSignin.setOnClickListener {
            val action = AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToSigninFragment()
            findNavController().navigate(action)
        }


          callbackManager = CallbackManager.Factory.create();


        val EMAIL = "email"

        val loginButton = binding.loginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
         loginButton.setFragment(this);

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Toast.makeText(requireActivity(), loginResult.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCancel() {
                Toast.makeText(requireActivity(), "cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(requireActivity(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root

    }


     override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


}