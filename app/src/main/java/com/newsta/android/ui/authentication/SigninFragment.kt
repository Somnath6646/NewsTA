package com.newsta.android.ui.authentication

import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context.ACCOUNT_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSigninBinding


class SigninFragment : Fragment() {


    private lateinit var binding: FragmentSigninBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate<FragmentSigninBinding>(inflater, R.layout.fragment_signin, container, false )

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }



        test()

        return binding.root
    }
    val REQUEST_CODE_EMAIL = 1;

    fun test(){



        // ...

        try {
            val intent = AccountPicker.newChooseAccountIntent(null, null,
             arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE) , false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch ( e: ActivityNotFoundException) {
            Log.i("Exception", e.toString())
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            binding.username.setText(accountName)
        }
    }








}