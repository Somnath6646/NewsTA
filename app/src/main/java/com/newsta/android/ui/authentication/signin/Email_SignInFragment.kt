package com.newsta.android.ui.authentication.signin


import android.accounts.AccountManager
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.material.textfield.TextInputEditText
import com.newsta.android.R
import com.newsta.android.databinding.FragmentEmailSignInBinding
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.ui.authentication.base.EmailFragment
import com.newsta.android.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.*


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