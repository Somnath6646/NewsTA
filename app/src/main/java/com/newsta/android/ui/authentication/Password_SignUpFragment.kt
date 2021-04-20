package com.newsta.android.ui.authentication


import com.newsta.android.databinding.FragmentEmailSignUpBinding
import com.newsta.android.ui.base.BaseFragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.FragmentPasswordSignUpBinding
import com.newsta.android.remote.data.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Password_SignUpFragment : BaseFragment<FragmentPasswordSignUpBinding>() {

    val viewModel by activityViewModels<AuthenticationViewmodel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.btnSignup.setOnClickListener {
            println("Email122 ${viewModel.email.value}")
            Log.i("Password", viewModel.password.value.toString())
            viewModel.signUp()
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.password.value = ""
        }



        viewModel.password.observe(viewLifecycleOwner, Observer {
            Log.i("password", it)
        })


        viewModel.signupResponse.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Signup Faliure ${it.errorCode}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun getFragmentView(): Int = R.layout.fragment_password__sign_up

}