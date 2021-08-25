package com.newsta.android.ui.authentication.signin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentSigninOptionsBinding
import com.newsta.android.ui.authentication.adapter.TutorialAdapter
import com.newsta.android.viewmodels.AuthenticationViewModel
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.utils.models.DataState
import java.util.*

class SignIn_AuthenticationOptionsFragment : BaseFragment<FragmentSigninOptionsBinding>() {


    private lateinit var callbackManager: CallbackManager
    val viewModel by activityViewModels<AuthenticationViewModel>()
    private lateinit var facebookAccessToken: String
    private lateinit var iss: String

    override fun getFragmentView(): Int = R.layout.fragment_signin_options

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnSignInEmail.setOnClickListener {
            val action = SignIn_AuthenticationOptionsFragmentDirections.actionSignInAuthenticationOptionsFragmentToEmailSignInFragment()
            findNavController().navigate(action)
        }

        binding.btnSignup.setOnClickListener {
            findNavController().popBackStack()
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

        val loginButton = binding.loginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
        loginButton.setFragment(this);


        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                if (loginResult != null) {
                    Log.i("Facebook Signin", loginResult.accessToken.token)

                    facebookAccessToken = loginResult.accessToken.token

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


        viewModel.signinResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled().let {
                when(it){
                    is DataState.Success -> {
                        Log.i("SIGN INNNN", "HOOOOOO GYA")
                        it.data?.data?.let { it1 -> viewModel.getUserPreferences(accessToken = it1) }
                    }
                    is DataState.Loading -> {
                        Log.i("SIGN INNNN", "LOD HO RHA H")
                    }
                    is DataState.Error -> {
                        Log.i("SIGN INNNN", "NAHI HUA")
                        LoginManager.getInstance().logOut()

                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                        dialog.setContentView(dialogBinding.root)

                        println("Abhi hai $dialogBinding")

                        dialogBinding.message.text = "${it.exception}"
                        dialogBinding.buttonText.text = "Sign Up"
                        dialogBinding.button.setOnClickListener { v ->
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }

                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                        dialog.show()
                    }
                    else -> {}
                }
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