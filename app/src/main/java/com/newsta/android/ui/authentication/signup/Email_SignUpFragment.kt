package com.newsta.android.ui.authentication.signup


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.newsta.android.R
import com.newsta.android.databinding.FragmentEmailSignUpBinding
import com.newsta.android.ui.authentication.base.EmailFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Email_SignUpFragment : EmailFragment<FragmentEmailSignUpBinding>() {
    override fun navigateToPasswordFragment() {
        val action = Email_SignUpFragmentDirections.actionEmailSignUpFragmentToPasswordSignUpFragment()
        findNavController().navigate(action)
    }

    override fun getInputEmailView(): TextInputEditText = binding.inputEmail

    override fun getFragmentView(): Int = R.layout.fragment_email__sign_up

    override fun getBackButtonView(): View = binding.btnBack

    override fun getNextButtonView(): View = binding.btnNext

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }


}
