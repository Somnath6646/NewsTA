package com.newsta.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.newsta.android.databinding.ActivityMainBinding
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel : AuthenticationViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel.userPrefrences.accessToken.asLiveData().observe(this, Observer {accessToken ->
            if (accessToken != null) {
                NewstaApp.access_token = accessToken
                NewstaApp.setAccessToken(accessToken)

                Log.i("MainActivity", (accessToken == NewstaApp.getAccessToken()).toString())
            }
        })

    }






}