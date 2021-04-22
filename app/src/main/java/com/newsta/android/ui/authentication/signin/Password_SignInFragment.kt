package com.newsta.android.ui.authentication.signin


import com.newsta.android.ui.base.BaseFragment


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.FragmentPasswordSignInBinding
import com.newsta.android.databinding.FragmentPasswordSignUpBinding
import com.newsta.android.remote.data.Resource
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Password_SignInFragment : BaseFragment<FragmentPasswordSignInBinding>() {

    val viewModel by activityViewModels<AuthenticationViewmodel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.btnSignin.setOnClickListener {
            println("Email122 ${viewModel.email.value}")
            Log.i("Password", viewModel.password.value.toString())
            viewModel.signIn()
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.password.value = ""
        }



        viewModel.password.observe(viewLifecycleOwner, Observer {
            Log.i("password", it)
        })


        viewModel.signinResponse.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Signin Faliure ${it.errorBody.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun getFragmentView(): Int = R.layout.fragment_password__sign_in

}