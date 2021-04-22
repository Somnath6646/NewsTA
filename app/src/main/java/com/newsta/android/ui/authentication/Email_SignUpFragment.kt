package com.newsta.android.ui.authentication


import android.accounts.AccountManager
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.newsta.android.R
import com.newsta.android.databinding.FragmentEmailSignUpBinding
import com.newsta.android.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Email_SignUpFragment : BaseFragment<FragmentEmailSignUpBinding>() {

    val REQUEST_CODE_EMAIL = 1

    val viewModel by activityViewModels<AuthenticationViewmodel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.inputEmail.isActivated = true
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setNextButtonDisabled(true)

        viewModel.email.observe(viewLifecycleOwner, Observer {
            println("Email ${viewModel.email.value}")
        })

        try {
            val intent = AccountManager.newChooseAccountIntent(
                null, null,
                arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), false, null, null, null, null
            )

            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (e: ActivityNotFoundException) {
            Log.i("Exception", e.toString())
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.email.value = ""
        }

        binding.btnNext.setOnClickListener {
            validateEmail(viewModel.email.value)
        }

        binding.inputEmail.addTextChangedListener {  text: Editable? ->

            val email = text.toString()
            setNextButtonDisabled(!isEmailValid(email))

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            binding.inputEmail.setText(accountName)
            validateEmail(accountName)
        }
    }

    private fun isEmailValid(email: String?) = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validateEmail(email: String?) {
        if(isEmailValid(email)) {
            navigateToPasswordFragment()
        } else {
            Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigateToPasswordFragment() {
        val action =
            Email_SignUpFragmentDirections.actionEmailSignUpFragmentToPasswordSignUpFragment()
        findNavController().navigate(action)
    }

    private fun setNextButtonDisabled(isDisabled: Boolean) {
        if(isDisabled) {
            binding.btnNext.alpha = 0.5f
            binding.btnNext.isEnabled = false
        } else {
            binding.btnNext.alpha = 1f
            binding.btnNext.isEnabled = true
        }
    }

    override fun getFragmentView(): Int = R.layout.fragment_email__sign_up

}
