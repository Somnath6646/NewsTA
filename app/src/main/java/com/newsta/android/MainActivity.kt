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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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

    private val MY_REQUEST_CODE: Int = 122
    val viewModel : AuthenticationViewModel by viewModels()
    val newsViewModel : NewsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @SuppressLint("LongLogTag", "HardwareIds")
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
        viewModel.userPrefrences.isDarkMode.asLiveData().distinctUntilChanged().observe(this, Observer { isDarkMode ->
            if (isDarkMode != null) {
                NewstaApp.isDarkMode = isDarkMode
                if(isDarkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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


        
        updateApp()

    }
    
    private fun updateApp(){
        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,

                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE)
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                Toast.makeText(this, "Update failed 😭", Toast.LENGTH_SHORT).show()
                updateApp()
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleConfigurationUtil.adjustFontSize(newBase!!, NewstaApp.font_scale!!))
    }

    companion object {

        var extras = ArrayList<MaxStoryAndUpdateTime>()
        lateinit var minStory: Story
        lateinit var maxStory: MaxStoryAndUpdateTime
        var isConnectedToNetwork = true
        var categoryState:Int = 0

    }

}
