package com.newsta.android.ui.authentication.signup

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentSignupOptionsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.utils.models.DataState
import com.newsta.android.viewmodels.AuthenticationViewModel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Base64.getEncoder


class SignUp_AuthenticationOptionsFragment : BaseFragment<FragmentSignupOptionsBinding>() {


    private lateinit var callbackManager: CallbackManager
    val viewModel by activityViewModels<AuthenticationViewModel>()

    override fun getFragmentView(): Int = R.layout.fragment_signup_options

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

//        ViewCompat.setTransitionName(binding.imageView, "logoTransition")

        binding.btnSignup.setOnClickListener {
            val action =
                    SignUp_AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToEmailSignUpFragment()
            findNavController().navigate(action)
        }



          callbackManager = CallbackManager.Factory.create();



        val EMAIL = "email"
        val loginButton = binding.loginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
        loginButton.setFragment(this);


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


        viewModel.signupResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled().let {
                when(it){
                    is DataState.Success -> {
                        Log.i("SIGN INNNN", "HOOOOOO GYA")
                        it.data?.data?.let { it1 -> viewModel.getUserPreferences(accessToken = it1) }
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "Loading")
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "eror")
                        LoginManager.getInstance().logOut();

                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                        dialog.setContentView(dialogBinding.root)

                        println("Abhi hai $dialogBinding")

                        dialogBinding.message.text = "${it.exception}"
                        dialogBinding.buttonText.text = "Sign In"
                        dialogBinding.button.setOnClickListener { v ->
                            dialog.dismiss()
                            val action = SignUp_AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToSignInAuthenticationOptionsFragment()
                            findNavController().navigate(action)
                        }

                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                        dialog.show()
                    }
                    else -> {}
                }
            }
        })





        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(loginResult: LoginResult?) {
                if (loginResult != null) {
                    Log.i("Facebook Signin", loginResult.accessToken.token)
                    viewModel.signUp(accessToken = loginResult.accessToken.token, iss = "facebook")

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

        binding.btnSignin.setOnClickListener {
            val action = SignUp_AuthenticationOptionsFragmentDirections.actionSignupSigninOptionsFragmentToSignInAuthenticationOptionsFragment()
            findNavController().navigate(action)
        }

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


    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info: PackageInfo = context?.packageManager!!.getPackageInfo(
                "com.newsta.android",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", java.util.Base64.getEncoder().encodeToString(md.digest()))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", e.toString())
        }
    }*/

}