package com.newsta.android.ui.authentication

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.remote.data.Resource
import com.newsta.android.remote.data.SigninRequest
import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import kotlinx.coroutines.launch


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
@Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Resource<SignupResponse>> = MutableLiveData()

    val signupResponse: LiveData <Resource<SignupResponse>>
    get() = _signupResponse



    private val _signinResponse: MutableLiveData<Resource<SigninResponse>> = MutableLiveData()

    val signinResponse: LiveData <Resource<SigninResponse>>
        get() = _signinResponse


    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    fun signUp(){

        viewModelScope.launch {

           _signupResponse.value =  authRepository.signup(SignupRequest(email.value!!, password = password.value!!))
        }

    }


    fun signIn(){

        viewModelScope.launch {

            _signinResponse.value =  authRepository.signin(SigninRequest(email.value!!, password = password.value!!))
        }

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}