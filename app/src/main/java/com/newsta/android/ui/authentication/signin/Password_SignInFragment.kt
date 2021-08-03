package com.newsta.android.ui.authentication.signin


import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.newsta.android.ui.base.BaseFragment


import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentPasswordSignInBinding
import com.newsta.android.viewmodels.AuthenticationViewModel
import com.newsta.android.utils.models.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Password_SignInFragment : BaseFragment<FragmentPasswordSignInBinding>() {

    val viewModel by activityViewModels<AuthenticationViewModel>()
    private val strengthWeak = 0
    private val strengthModerate = 1
    private val strengthStrong = 2
    private val strengthVeryStrong = 3

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.btnSignin.setOnClickListener {
            println("Email122 ${viewModel.email.value}")
            Log.i("Password", viewModel.password.value.toString())
            viewModel.signIn()
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.password.value = ""
        }



        viewModel.password.observe(viewLifecycleOwner, Observer {
            Log.i("password", it)
        })


        viewModel.navigate.observe(viewLifecycleOwner, Observer {
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


        viewModel.signinResponse.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                when(it){
                    is DataState.Success -> {
                        binding.ctabtnProgressBar.visibility = View.GONE
                        it.data?.data?.let { it1 ->
                            viewModel.getUserPreferences(accessToken = it1)
                        }
                    }
                    is DataState.Loading -> {
                        binding.ctabtnProgressBar.visibility = View.VISIBLE
                    }
                    is DataState.Error -> {
                        binding.ctabtnProgressBar.visibility = View.GONE

                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                        dialog.setContentView(dialogBinding.root)

                        println("Abhi hai $dialogBinding")

                        dialogBinding.message.text = "${it.exception}"
                        dialogBinding.buttonText.text = "Try Again"
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


        showPasswordStrength()

    }


    fun navigateToMainFragment(){
        val action = Password_SignInFragmentDirections.actionPasswordSignInFragmentToLandingFragment()
        findNavController().navigate(action)
    }



    private fun showPasswordStrength() {
        binding.inputPassword.addTextChangedListener { text ->

            val password = text.toString()
            val strength = passwordStrength(password)

            when(strength) {

                strengthWeak -> {
                    binding.textFieldPassword.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.password_weak)))
                    binding.textFieldPassword.hint = "Weak"
                }

                strengthModerate -> {
                    binding.textFieldPassword.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.password_moderate)))
                    binding.textFieldPassword.hint = "Moderate"
                }

                strengthStrong -> {
                    binding.textFieldPassword.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.password_strong)))
                    binding.textFieldPassword.hint = "Strong"
                }

                strengthVeryStrong -> {
                    binding.textFieldPassword.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.password_very_strong)))
                    binding.textFieldPassword.hint = "Very strong"
                }

            }

        }
    }

    private fun passwordStrength(passwordToCheck: String?): Int {

        var hasSpecial = false
        var hasDigit = false
        var hasUppercase = false
        var hasLowercase = false

        var strength = 0

        val password = passwordToCheck
        password?.forEach { c ->

            if (!hasSpecial && !c.isLetterOrDigit()) {
                strength++
                hasSpecial = true
            } else {
                if (!hasDigit && c.isDigit()) {
                    strength++
                    hasDigit = true
                } else {
                    if (!hasUppercase && c.isUpperCase()) {
                        strength++
                        hasUppercase = true
                    } else if (!hasLowercase && c.isLowerCase()) {
                        hasLowercase = true
                    }
                }
            }

        }

        return strength

    }

    private fun validatePassword(password: String?): Boolean {
        val strength = passwordStrength(password)
        if(!password.isNullOrEmpty()) {
            if(password.length >= 8 && strength >= strengthStrong) {
                return true
            }
        }
        return false
    }


    override fun getFragmentView(): Int = R.layout.fragment_password__sign_in

}