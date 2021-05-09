package com.newsta.android.ui.authentication.base

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.newsta.android.R
import com.newsta.android.ui.authentication.AuthenticationViewmodel

abstract class PasswordFragment <T: ViewDataBinding>: Fragment() {

    protected lateinit var binding: T
    val viewModel by activityViewModels<AuthenticationViewmodel>()
    private val strengthWeak = 0
    private val strengthModerate = 1
    private val strengthStrong = 2
    private val strengthVeryStrong = 3

    private lateinit var  passwordInputEditText: TextInputEditText
    private lateinit var  passwordInputLayout: TextInputLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                getFragmentView(),
                container,
                false
        )


        passwordInputEditText = getPasswordInputEditText()
        passwordInputLayout = getPasswordInputEditLayout()

        getCTAButtonView().setOnClickListener {
            getCTAButtonAction()

        }


        getBackButtonView().setOnClickListener {
            findNavController().popBackStack()
            viewModel.password.value = ""
        }

        viewModel.navigate.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if(it!=null){
                    when(it){
                        "Landing" -> {
                            navigateToMainFragment()
                            binding.root.clearFocus()
                        }
                    }
                }

            }

        })

        showPasswordStrength()



        return binding.root
    }

    private fun showPasswordStrength() {


        passwordInputEditText.addTextChangedListener { text: Editable? ->

            val password = text.toString()
            val strength = passwordStrength(password)

            when(strength) {

                strengthWeak -> {
                    setPasswordLayoutColorAndHint(R.color.password_weak, "Weak")
                }

                strengthModerate -> {
                    setPasswordLayoutColorAndHint(R.color.password_moderate, "Moderate")
                }

                strengthStrong -> {
                    setPasswordLayoutColorAndHint(R.color.password_strong, "Strong")
                }

                strengthVeryStrong -> {
                    setPasswordLayoutColorAndHint(R.color.password_very_strong, "Very strong")
                }

            }

        }
    }


    private fun setPasswordLayoutColorAndHint( color: Int, hint: String){
        passwordInputLayout.setHintTextColor(ColorStateList.valueOf(resources.getColor(color)))
        passwordInputLayout.hint = hint
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

    protected fun validatePassword(password: String?): Boolean {
        val strength = passwordStrength(password)
        if(!password.isNullOrEmpty()) {
            if(password.length >= 8 && strength >= strengthStrong) {
                return true
            }
        }
        return false
    }


    abstract fun getPasswordInputEditText(): TextInputEditText
    abstract fun getPasswordInputEditLayout(): TextInputLayout
    abstract fun navigateToMainFragment()
    abstract fun getBackButtonView(): View
    abstract fun getCTAButtonView() : View
    abstract fun getCTAButtonAction()
    abstract fun getFragmentView(): Int
}
