package com.newsta.android.ui.authentication.base

import android.accounts.AccountManager
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.material.textfield.TextInputEditText
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.ui.base.BaseFragment

abstract class EmailFragment <T: ViewDataBinding>: Fragment(){
    protected lateinit var binding: T

    val REQUEST_CODE_EMAIL = 1

    val viewModel by activityViewModels<AuthenticationViewmodel>()

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

        getInputEmailView().isActivated = true
        setNextButtonDisabled(true)


        try {
            val intent = AccountManager.newChooseAccountIntent(
                    null, null,
                    arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), false, null, null, null, null
            )

            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (e: ActivityNotFoundException) {
            Log.i("Exception", e.toString())
        }


        getBackButtonView().setOnClickListener {
            findNavController().popBackStack()
            viewModel.email.value = ""
            viewModel.password.value =""
        }

        getNextButtonView().setOnClickListener {
            navigateToPasswordFragment()
        }


        viewModel.email.observe(viewLifecycleOwner, Observer {text: String ->
            val email = text.toString()
            setNextButtonDisabled(!isEmailValid(email))
        })

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.email.value = ""
        viewModel.password.value = ""
    }

    private fun setNextButtonDisabled(isDisabled: Boolean) {
        if(isDisabled) {
            getNextButtonView().alpha = 0.5f
            getNextButtonView().isEnabled = false
        } else {
            getNextButtonView().alpha = 1f
            getNextButtonView().isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            getInputEmailView().setText(accountName)
            navigateToPasswordFragment()
        }
    }


    private fun isEmailValid(email: String?) = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()


    abstract fun navigateToPasswordFragment()


    abstract fun getInputEmailView(): TextInputEditText
    abstract fun getFragmentView(): Int
    abstract fun getBackButtonView(): View
    abstract fun getNextButtonView(): View
}