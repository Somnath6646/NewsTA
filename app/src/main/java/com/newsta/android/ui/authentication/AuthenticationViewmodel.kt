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
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
            private val prefrences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Indicator<DataState<SignupResponse?>>> = MutableLiveData()

    val signupResponse: LiveData <Indicator<DataState<SignupResponse?>>>
        get() = _signupResponse

    val userPrefrences: UserPrefrences
        get() = prefrences


    private val _signinResponse: MutableLiveData<Indicator<DataState<SigninResponse?>>> = MutableLiveData()



    val signinResponse: LiveData <Indicator<DataState<SigninResponse?>>>
        get() = _signinResponse



    private val _facebookSignInResponse: MutableLiveData<Indicator<DataState<SigninResponse?>>> = MutableLiveData()



    val facebookSignInResponse: LiveData <Indicator<DataState<SigninResponse?>>>
        get() = _facebookSignInResponse

    private val _navigate: MutableLiveData<Indicator<String>> = MutableLiveData()

    val navigate: LiveData<Indicator<String>>
        get() = _navigate

    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    fun signUp(){

        viewModelScope.launch {

            authRepository.signup(SignupRequest(email.value!!, password = password.value!!)).onEach {
                _signupResponse.value = Indicator(it)
            }.launchIn(viewModelScope)

        }


    }


    fun signIn(){

        viewModelScope.launch {

            authRepository.signin(SigninRequest(email.value!!, password = password.value!!)).onEach {
                _signinResponse.value = Indicator(it)
            }.launchIn(viewModelScope)

        }

    }


    fun signIn(accessToken: String, iss: String){

        viewModelScope.launch{
            authRepository.signin(SignInRequest_Social(iss = iss, access_token = accessToken)).onEach {
                _signinResponse.value = Indicator(it)
            }.launchIn(viewModelScope)

        }
    }

    fun signUp(accessToken: String, iss: String){

        viewModelScope.launch{

            authRepository.signup(SignUpRequest_Social(iss = iss, access_token = accessToken)).onEach {
                _signupResponse.value = Indicator(it)
            }.launchIn(viewModelScope)

        }
    }

    fun saveTokenAndIss(accessToken: String){

        viewModelScope.launch {
            userPrefrences.saveAccessToken(accessToken)
            userPrefrences.isDatabaseEmpty(true)
            _navigate.value =
                Indicator("Landing")
        }

    }



    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}