package com.newsta.android

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import com.newsta.android.databinding.ActivityMainBinding
import com.newsta.android.viewmodels.AuthenticationViewModel
import com.newsta.android.utils.NetworkObserver
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.helpers.LocaleConfigurationUtil
import com.newsta.android.utils.models.MaxStoryAndUpdateTime
import com.newsta.android.utils.models.Story
import com.newsta.android.utils.workers.DatabaseClearer
import com.newsta.android.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    val viewModel : AuthenticationViewModel by viewModels()
    val newsViewModel : NewsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @SuppressLint("LongLogTag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleConfigurationUtil.adjustFontSize(this, NewstaApp.font_scale!!)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel.userPrefrences.accessToken.asLiveData().observe(this, Observer { accessToken ->
            NewstaApp.access_token = accessToken
            NewstaApp.setAccessToken(accessToken)
            viewModel.incrementObserverCount()

            if (!NewstaApp.access_token.isNullOrEmpty()) {}
            Log.i("MainActivity", (accessToken == NewstaApp.getAccessToken()).toString())
        })

        viewModel.userPrefrences.isDatabaseEmpty.asLiveData().observe(this, Observer { isDatabaseEmpty ->
            if (isDatabaseEmpty != null) {
                NewstaApp.is_database_empty = isDatabaseEmpty
                NewstaApp.setIsDatabaseEmpty(isDatabaseEmpty)
                viewModel.incrementObserverCount()

                Log.i("MainActivity IS DB EMPTY --->", (NewstaApp.getIsDatabaseEmpty()).toString())
                Log.i("MainActivity IS DB EMPTY", (isDatabaseEmpty == NewstaApp.getIsDatabaseEmpty()).toString())
            } else {
                Log.i("MainActivity IS DB EMPTY", "null")
            }
        })

        viewModel.userPrefrences.fontScale.asLiveData().observe(this, Observer { fontScale ->

            if(fontScale != null) {

                NewstaApp.font_scale = fontScale
                NewstaApp.setFontScale(fontScale)
                viewModel.incrementObserverCount()

                LocaleConfigurationUtil.adjustFontSize(this, NewstaApp.font_scale!!)

                Log.i("MainActivity", (fontScale == NewstaApp.getFontScale()).toString())

            }

        })

        viewModel.userPrefrences.hasChangedPreferences.asLiveData().observe(this, Observer { hasChanged ->

            if(hasChanged != null) {

                NewstaApp.has_changed_preferences = hasChanged
                NewstaApp.setHasChangedPreferences(hasChanged)
                Log.i("MainActivity", (hasChanged == NewstaApp.getHasChangedPreferences()).toString())
                viewModel.incrementObserverCount()

            }

        })

        viewModel.observerCount.observe(this, Observer {
            if(it == 4) {
                newsTasks()
                viewModel.observerCount.removeObservers(this)
            }
        })

        observeUserNetworkConnection()

    }

    private fun newsTasks() {
        searchWithNewsta()
        println("NEWS VIEW MODEL IN MAIN ---> $newsViewModel")
        println("NEWSTA ACCESS TOKEN ---> ${NewstaApp.access_token}")



        newsViewModel.toast.observe(this, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        })

        newsViewModel.toast("MAIN ACTIVITY")
    }

    private fun searchWithNewsta() {
        if (intent?.action == Intent.ACTION_SEND) {
            println("SHARE IF MEIN AAYA HAI")
            println("SHARE INTENT: $intent")
            if (intent.type?.startsWith("text/") == true) {
                println("SHARE INTENT: $intent")
                intent?.getStringExtra(Intent.EXTRA_TEXT)?.let { data ->
                    println("INTENT SHARE TEXT: $data")
                    val dataToSearch = bundleOf("dataToSearch" to data)
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.action_splashFragment_to_searchFragment, dataToSearch)
                }
            }
        }
    }

    private fun observeUserNetworkConnection() {

        NetworkObserver.getNetworkLiveData(applicationContext)
            .observe(this, androidx.lifecycle.Observer { isConnected ->
                if (!isConnected) {
                    isConnectedToNetwork = isConnected
                    binding.textViewNetworkStatus.text = "No internet connection"
                    binding.networkStatusLayout.apply {
                        binding.networkStatusLayout.visibility = View.VISIBLE
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_red_light
                            )
                        )
                    }
                } else {
                    isConnectedToNetwork = isConnected
                    binding.textViewNetworkStatus.text = "Back Online"

                    binding.networkStatusLayout.apply {
                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    binding.networkStatusLayout.visibility = View.GONE
                                }
                            }).start()
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            })
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleConfigurationUtil.adjustFontSize(newBase!!, NewstaApp.font_scale!!))
    }

    companion object {

        var extras = ArrayList<MaxStoryAndUpdateTime>()
        lateinit var minStory: Story
        lateinit var maxStory: MaxStoryAndUpdateTime
        var isConnectedToNetwork = true
        var categoryState = 0

    }

}
