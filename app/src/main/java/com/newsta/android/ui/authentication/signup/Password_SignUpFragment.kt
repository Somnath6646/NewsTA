package com.newsta.android.ui.authentication.signup


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentPasswordSignUpBinding
import com.newsta.android.ui.authentication.base.PasswordFragment
import com.newsta.android.utils.models.DataState
import dagger.hilt.android.AndroidEntryPoint

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
                    is DataState.Success -> {
                        Log.i("TAG", "Sucess")
                        binding.ctabtnProgressBar.visibility = View.GONE
                        it.data?.data?.let { it1 ->
                            viewModel.getUserPreferences(accessToken = it1)
                        }
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "Loading")
                        binding.ctabtnProgressBar.visibility = View.VISIBLE
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "eror")
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



    }

}