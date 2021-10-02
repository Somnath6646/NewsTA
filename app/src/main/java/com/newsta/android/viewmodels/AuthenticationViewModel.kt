package com.newsta.android.viewmodels

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.*
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.SigninResponse
import com.newsta.android.responses.SignupResponse
import com.newsta.android.responses.SkipAuthResponse
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class AuthenticationViewModel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
            private val prefrences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Indicator<DataState<SignupResponse?>>> = MutableLiveData()
    val signupResponse: LiveData <Indicator<DataState<SignupResponse?>>>
        get() = _signupResponse

    private val _skipAuthResponse: MutableLiveData<Indicator<DataState<SkipAuthResponse?>>> = MutableLiveData()
    val skipAuthResponse: LiveData <Indicator<DataState<SkipAuthResponse?>>>
        get() = _skipAuthResponse

    val userPrefrences: UserPrefrences
        get() = prefrences


    private val _signinResponse: MutableLiveData<Indicator<DataState<SigninResponse?>>> = MutableLiveData()



    val signinResponse: LiveData <Indicator<DataState<SigninResponse?>>>
        get() = _signinResponse

    private val _navigate: MutableLiveData<Indicator<String>> = MutableLiveData()
    val navigate: LiveData<Indicator<String>>
        get() = _navigate

    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    private val _observerCount = MutableLiveData<Int>()
    val observerCount: LiveData<Int>
        get() = _observerCount

    fun incrementObserverCount() {
        _observerCount.value = _observerCount.value?.plus(1)
    }

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

    fun skipAuth(deviceID: String){

        viewModelScope.launch{
            authRepository.skipAuth(SkipAuthRequest(deviceID)).onEach {
                _skipAuthResponse.value = Indicator(it)
            }.launchIn(viewModelScope)

        }
    }

    fun getUserPreferences(accessToken: String) {
        viewModelScope.launch {
            authRepository.getUserPreferences(UserPreferencesRequest(accessToken, NewstaApp.ISSUER_NEWSTA)).onEach {
                println("USER PREFERENCE DATA ---> $it")
                when (it) {
                    is DataState.Success -> {
                        saveTokenAndIss(accessToken, true)
                    }

                    is DataState.Error -> {
                        if(it.statusCode == 499) {
                            saveTokenAndIss(accessToken, false)
                        } else {
                            println("ERROR IN AUTH ---> ${it.exception}")
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveTokenAndIss(accessToken: String, hasChangedPreferences: Boolean = false){

        viewModelScope.launch {
            userPrefrences.saveAccessToken(accessToken)
            userPrefrences.isDatabaseEmpty(true)
            userPrefrences.setFontScale(NewstaApp.DEFAULT_FONT_SCALE)
            userPrefrences.hasChangedPreferences(hasChangedPreferences)
            _navigate.value = Indicator("Landing")
        }

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
