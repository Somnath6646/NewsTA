package com.newsta.android.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSignUpBinding
import com.newsta.android.remote.data.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel : AuthenticationViewmodel by viewModels()
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        viewModel.signupResponse.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Login Faliure ${it.errorCode}", Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.btnSignup.setOnClickListener {
            val email = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()
            viewModel.signUp(email, password)
        }

        return binding.root
    }


}