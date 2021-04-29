package com.newsta.android.ui.authentication.signup


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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentPasswordSignUpBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.ui.authentication.base.PasswordFragment
import com.newsta.android.ui.authentication.signin.Password_SignInFragment
import com.newsta.android.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_landing.*

@AndroidEntryPoint
class Password_SignUpFragment : PasswordFragment<FragmentPasswordSignUpBinding>(){

    override fun getPasswordInputEditText(): TextInputEditText = binding.inputPassword

    override fun getPasswordInputEditLayout(): TextInputLayout = binding.textFieldPassword

    override fun navigateToMainFragment() {
        val action = Password_SignUpFragmentDirections.actionPasswordSignUpFragmentToLandingFragment()
        findNavController().navigate(action)
    }

    override fun getBackButtonView(): View = binding.btnBack

    override fun getCTAButtonView(): View = binding.btnSignup

    override fun getCTAButtonAction() {
        if(validatePassword(viewModel.password.value)) {
            viewModel.signUp()
        } else {
            Toast.makeText(context, "Enter a strong password", Toast.LENGTH_SHORT).show()
            binding.passwordMessage.visibility = View.VISIBLE
        }
    }

    override fun getFragmentView(): Int = R.layout.fragment_password__sign_up

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.signupResponse.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {

                when(it){
                    is Resource.Success -> {
                        viewModel.saveToken(accessToken = it.data.data)
                    }

                    is Resource.Failure -> {

                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()

                        when(it.errorCode) {
                            400 -> {
                                val dialog = Dialog(requireContext())
                                val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
                                dialog.setContentView(dialogBinding.root)

                                println("Abhi hai $dialogBinding")

                                dialogBinding.message.text = "User already registered"
                                dialogBinding.buttonText.text = "Try Again"
                                dialogBinding.button.setOnClickListener { v ->
                                    dialog.dismiss()
                                    findNavController().popBackStack()
                                }

                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                                dialog.show()
                            }
                        }
                    }
                }

            }

        })


    }

}