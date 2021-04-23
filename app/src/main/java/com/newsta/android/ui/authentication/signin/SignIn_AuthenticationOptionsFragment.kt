package com.newsta.android.ui.authentication.signin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentSignInAuthenticationOptionsBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.ui.base.BaseFragment
import java.util.*

class SignIn_AuthenticationOptionsFragment : BaseFragment<FragmentSignInAuthenticationOptionsBinding>() {


    private lateinit var callbackManager: CallbackManager
    val viewModel by activityViewModels<AuthenticationViewmodel>()



    override fun getFragmentView(): Int = R.layout.fragment_sign_in__authentication_options

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnSignInEmail.setOnClickListener {
            val action = SignIn_AuthenticationOptionsFragmentDirections.actionSignInAuthenticationOptionsFragmentToEmailSignInFragment()
            findNavController().navigate(action)
        }

        binding.btnSignup.setOnClickListener {
            val action = SignIn_AuthenticationOptionsFragmentDirections.actionSignInAuthenticationOptionsFragmentToSignupSigninOptionsFragment()
            findNavController().navigate(action)
        }

        callbackManager = CallbackManager.Factory.create();


        val EMAIL = "email"

        viewModel.navigate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled().let {
                if(it!=null){
                    when(it){
                        "Landing" -> {
                            navigateToMainFragment()
                        }
                    }
                }

            }

        })


        viewModel.signinResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled().let {
                when(it) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()

                        println("ACcessToken ${it.data.data}")
                        viewModel.saveToken(it.data.data)


                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Facebook error code: ${it.errorCode}", Toast.LENGTH_SHORT).show()
                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                        dialog.setContentView(dialogBinding.root)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        when(it.errorCode) {
                            400 -> {
                                dialogBinding.message.text = "User not registered"
                                dialogBinding.buttonText.text = "Try Again"
                                dialogBinding.button.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        }

                    }
                }
            }

        })


        val loginButton = binding.loginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
        loginButton.setFragment(this);


        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                if (loginResult != null) {
                    Log.i("Facebook Signin", loginResult.accessToken.token)
                    viewModel.signIn(accessToken = loginResult.accessToken.token, iss = "facebook")

                }else{
                    Log.i("Facebook Signin", "null")
                }
            }

            override fun onCancel() {
                Toast.makeText(requireActivity(), "Sign in with facebook cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(requireActivity(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }


    fun navigateToMainFragment(){
        val action = SignIn_AuthenticationOptionsFragmentDirections.actionSignInAuthenticationOptionsFragmentToLandingFragment()
        findNavController().navigate(action)
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