package com.newsta.android.ui.authentication

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.remote.data.Resource
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.LoginResponse
import kotlinx.coroutines.launch


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
@Assisted private val savedStateHandle: SavedStateHandle): ViewModel() {

    private val _signupResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    val signupResponse: LiveData <Resource<LoginResponse>>
    get() = _signupResponse

    fun signUp(email: String, password: String){

        viewModelScope.launch {
           _signupResponse.value =  authRepository.signup(email, password)
        }

    }

}