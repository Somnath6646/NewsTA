package com.newsta.android.ui.authentication


import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentPasswordSignUpBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Password_SignUpFragment : BaseFragment<FragmentPasswordSignUpBinding>() {

    val viewModel by activityViewModels<AuthenticationViewmodel>()

    private val strengthWeak = 0
    private val strengthModerate = 1
    private val strengthStrong = 2
    private val strengthVeryStrong = 3


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.btnSignup.setOnClickListener {
            println("Email122 ${viewModel.email.value}")
            Log.i("password", "${viewModel.password.value.toString()} -----> Strength: ${passwordStrength(viewModel.password.value.toString())}")
            
            if(validatePassword(viewModel.password.value)) {
                viewModel.signUp()
            } else {
                Toast.makeText(context, "Enter a strong password", Toast.LENGTH_SHORT).show()
                binding.passwordMessage.visibility = View.VISIBLE
            }
            
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.password.value = ""
        }

        viewModel.password.observe(viewLifecycleOwner, Observer { password ->
            Log.i("password", "$password -----> Strength: ${passwordStrength(password)}")
        })

        viewModel.signupResponse.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Signup Faliure ${it.errorCode}", Toast.LENGTH_SHORT).show()

                    val dialog = Dialog(requireContext())
                    val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                    dialog.setContentView(dialogBinding.root)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.setContentView(R.layout.auth_dialog)
                    when(it.errorCode) {
                        400 -> {
                            dialogBinding.message.text = "Account with this email already exists"
                            dialogBinding.buttonText.text = "Try Again"
                            dialogBinding.button.setOnClickListener { v ->
                                findNavController().popBackStack()
                            }
                            dialog.show()
                        }
                    }

                }
            }
        })

        showPasswordStrength()

    }

    private fun showPasswordStrength() {
        binding.inputPassword.addTextChangedListener { text: Editable? ->

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

    override fun getFragmentView(): Int = R.layout.fragment_password__sign_up

}