package com.newsta.android.ui.authentication

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.remote.data.Resource
import com.newsta.android.remote.data.SignupRequest
import com.newsta.android.repository.AuthRepository
import com.newsta.android.responses.LoginResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class AuthenticationViewmodel
@ViewModelInject
constructor(private val authRepository: AuthRepository,
@Assisted private val savedStateHandle: SavedStateHandle): ViewModel(), Observable {

    private val _signupResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    val signupResponse: LiveData <Resource<LoginResponse>>
    get() = _signupResponse


    @Bindable
    val email = MutableLiveData<String>("")

    @Bindable
    val password = MutableLiveData<String>("")

    fun signUp(){

        viewModelScope.launch {

           _signupResponse.value =  authRepository.signup(SignupRequest(email.value!!, password = password.value!!))
        }

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}