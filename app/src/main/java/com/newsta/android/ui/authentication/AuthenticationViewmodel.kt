package com.newsta.android.ui.authentication

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.remote.data.Resource
import com.newsta.android.remote.data.SignInRequest_Social
import com.newsta.android.remote.data.SigninRequest
import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import com.newsta.android.utils.Event
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.launch


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
            private val prefrences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Event<Resource<SignupResponse>>> = MutableLiveData()

    val signupResponse: LiveData <Event<Resource<SignupResponse>>>
        get() = _signupResponse

    val userPrefrences: UserPrefrences
        get() = prefrences


    private val _signinResponse: MutableLiveData<Event<Resource<SigninResponse>>> = MutableLiveData()


    private val _navigate: MutableLiveData<Event<String>> = MutableLiveData()

    val navigate: LiveData<Event<String>>
    get() = _navigate

    val signinResponse: LiveData <Event<Resource<SigninResponse>>>
        get() = _signinResponse


    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    fun signUp(){

        viewModelScope.launch {

            _signupResponse.value = Event(authRepository.signup(SignupRequest(email.value!!, password = password.value!!)))
        }


    }


    fun signIn(){

        viewModelScope.launch {

            _signinResponse.value =  Event(authRepository.signin(SigninRequest(email.value!!, password = password.value!!)))
        }

    }


    fun signIn(accessToken: String, iss: String){
        viewModelScope.launch{
            _signinResponse.value = Event(authRepository.signin(SignInRequest_Social(iss = iss, access_token = accessToken)))
        }
    }

    fun saveToken(accessToken: String){

        viewModelScope.launch {
            userPrefrences.saveAccessToken(accessToken)
            _navigate.value = Event("Landing")
        }

    }



    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}