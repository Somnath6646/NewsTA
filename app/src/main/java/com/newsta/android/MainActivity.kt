package com.newsta.android

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.newsta.android.NewstaApp.Companion.font_scale
import com.newsta.android.databinding.ActivityMainBinding
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.utils.helpers.LocaleConfigurationUtil
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.Story
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel : AuthenticationViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleConfigurationUtil.adjustFontSize(this, font_scale!!)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel.userPrefrences.accessToken.asLiveData().observe(this, Observer {accessToken ->
            if (accessToken != null) {
                NewstaApp.access_token = accessToken
                NewstaApp.setAccessToken(accessToken)

                Log.i("MainActivity", (accessToken == NewstaApp.getAccessToken()).toString())
            }
        })

        viewModel.userPrefrences.isDatabaseEmpty.asLiveData().observe(this, Observer { isDatabaseEmpty ->
            if (isDatabaseEmpty != null) {
                NewstaApp.is_database_empty = isDatabaseEmpty
                NewstaApp.setIsDatabaseEmpty(isDatabaseEmpty)

                Log.i("MainActivity", (isDatabaseEmpty == NewstaApp.getIsDatabaseEmpty()).toString())
            }
        })

        viewModel.userPrefrences.fontScale.asLiveData().observe(this, Observer { fontScale ->

            if(fontScale != null) {

                NewstaApp.font_scale = fontScale
                NewstaApp.setFontScale(fontScale)

                Log.i("MainActivity", (fontScale == NewstaApp.getFontScale()).toString())

            }

        })

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleConfigurationUtil.adjustFontSize(newBase!!, font_scale!!))
    }

    companion object {

        var extras = ArrayList<Story>()
        lateinit var minStory: Story
        lateinit var maxStory: Story

    }

}
