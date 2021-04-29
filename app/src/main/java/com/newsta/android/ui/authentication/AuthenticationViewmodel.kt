package com.newsta.android.ui.authentication

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.remote.data.*
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import com.newsta.android.utils.Indicator
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.launch


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
            private val prefrences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Indicator<Resource<SignupResponse>>> = MutableLiveData()

    val signupResponse: LiveData <Indicator<Resource<SignupResponse>>>
        get() = _signupResponse

    val userPrefrences: UserPrefrences
        get() = prefrences


    private val _signinResponse: MutableLiveData<Indicator<Resource<SigninResponse>>> = MutableLiveData()


    private val _navigate: MutableLiveData<Indicator<String>> = MutableLiveData()

    val navigate: LiveData<Indicator<String>>
    get() = _navigate

    val signinResponse: LiveData <Indicator<Resource<SigninResponse>>>
        get() = _signinResponse


    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    fun signUp(){

        viewModelScope.launch {

            _signupResponse.value = Indicator(authRepository.signup(SignupRequest(email.value!!, password = password.value!!)))
        }


    }


    fun signIn(){

        viewModelScope.launch {

            _signinResponse.value =  Indicator(authRepository.signin(SigninRequest(email.value!!, password = password.value!!)))
        }

    }


    fun signIn(accessToken: String, iss: String){
        viewModelScope.launch{
            _signinResponse.value = Indicator(authRepository.signin(SignInRequest_Social(iss = iss, access_token = accessToken)))
        }
    }

    fun signUp(accessToken: String, iss: String){
        viewModelScope.launch{
            _signupResponse.value = Indicator(authRepository.signup(SignUpRequest_Social(iss = iss, access_token = accessToken)))
        }
    }

    fun saveToken(accessToken: String){

        viewModelScope.launch {
            userPrefrences.saveAccessToken(accessToken)
            _navigate.value = Indicator("Landing")
        }

    }



    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}