package com.newsta.android

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.newsta.android.databinding.ActivityMainBinding
import com.newsta.android.ui.authentication.AuthenticationViewmodel
import com.newsta.android.utils.NetworkObserver
import com.newsta.android.utils.models.Story
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel : AuthenticationViewmodel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel.userPrefrences.accessToken.asLiveData().observe(this, Observer {accessToken ->

                NewstaApp.access_token = accessToken

                NewstaApp.setAccessToken(accessToken)


                Log.i("MainActivity", (accessToken == NewstaApp.getAccessToken()).toString())

        })




        viewModel.userPrefrences.isDatabaseEmpty.asLiveData().observe(this, Observer { isDatabaseEmpty ->
            if (isDatabaseEmpty != null) {
                NewstaApp.is_database_empty = isDatabaseEmpty
                NewstaApp.setIsDatabaseEmpty(isDatabaseEmpty)

                Log.i("MainActivity", (isDatabaseEmpty == NewstaApp.getIsDatabaseEmpty()).toString())
            }
        })

        observeUserNetworkConnection()

    }


    private fun observeUserNetworkConnection(){

        NetworkObserver.getNetworkLiveData(applicationContext).observe(this, androidx.lifecycle.Observer { isConnected ->
            if(!isConnected){
                binding.textViewNetworkStatus.text = "No internet connection"
                binding.networkStatusLayout.apply {
                    binding.networkStatusLayout.visibility = View.VISIBLE
                    setBackgroundColor( ContextCompat.getColor(context,
                            android.R.color.holo_red_light
                    ))
                }
            } else{
                binding.textViewNetworkStatus.text = "Back Online"

                binding.networkStatusLayout.apply {
                    animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator?) {
                                    binding.networkStatusLayout.visibility = View.GONE
                                }
                            }).start()
                    setBackgroundColor( ContextCompat.getColor(context,
                            R.color.colorPrimary
                    ))
                }
            }
        })
    }

    companion object {

        var extras = ArrayList<Story>()
        lateinit var minStory: Story
        lateinit var maxStory: Story

    }

}
