package com.newsta.android.ui.authentication.signup

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
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
import com.newsta.android.databinding.FragmentSignupOptionsBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import java.util.*


class SignUp_AuthenticationOptionsFragment : Fragment() {


    private lateinit var callbackManager: CallbackManager
    val viewModel by activityViewModels<AuthenticationViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentSignupOptionsBinding>(inflater,
            R.layout.fragment_signup_options, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        ViewCompat.setTransitionName(binding.imageView, "logoTransition")

        binding.btnSignup.setOnClickListener {
            val action =
                    SignUp_AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToEmailSignUpFragment()
            findNavController().navigate(action)
        }

        binding.btnSignin.setOnClickListener {
            /*val action = AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToEmailSignInFragment()
            findNavController().navigate(action)*/
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

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
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

        return binding.root

    }


    fun navigateToMainFragment(){
        val action = SignUp_AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToLandingFragment()
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