package com.newsta.android.ui.authentication


import android.accounts.AccountManager
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import com.newsta.android.databinding.FragmentEmailSignUpBinding
import com.newsta.android.ui.base.BaseFragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.newsta.android.R
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

        viewModel.email.observe(viewLifecycleOwner, Observer {
            println("Email ${viewModel.email.value}")
        })

        try {
            val intent = AccountPicker.newChooseAccountIntent(null, null,
                arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE) , false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch ( e: ActivityNotFoundException) {
            Log.i("Exception", e.toString())
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.email.value = ""
        }



        binding.btnNext.setOnClickListener {
           navigateToPasswordFragment()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            binding.inputEmail.setText(accountName)
            navigateToPasswordFragment()
        }
    }

    fun navigateToPasswordFragment(){
        val action = Email_SignUpFragmentDirections.actionEmailSignUpFragmentToPasswordSignUpFragment()
        findNavController().navigate(action)
    }


    override fun getFragmentView(): Int = R.layout.fragment_email__sign_up

}