package com.newsta.android.ui.authentication.signin


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.newsta.android.R
import com.newsta.android.databinding.FragmentEmailSignInBinding
import com.newsta.android.ui.authentication.base.EmailFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Email_SignInFragment : EmailFragment<FragmentEmailSignInBinding>() {


    override fun navigateToPasswordFragment() {
        val action = Email_SignInFragmentDirections.actionEmailSignInFragmentToPasswordSignInFragment()
        findNavController().navigate(action)
    }

    override fun getInputEmailView(): TextInputEditText = binding.inputEmail

    override fun getFragmentView(): Int = R.layout.fragment_email__sign_in

    override fun getBackButtonView(): View = binding.btnBack

    override fun getNextButtonView(): View = binding.btnNext

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel= viewModel
        binding.lifecycleOwner =this
    }


}