package com.newsta.android.ui.details.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.interfaces.DetailsBottomNavInterface
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.remote.data.Payload
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.adapter.*
import com.newsta.android.ui.main.fragments.MainFragment
import com.newsta.android.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import com.newsta.android.utils.models.*

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private lateinit var story: Story
    private lateinit var stories: List<Story>
    private  var position: Int = 0
    private lateinit var data: DetailsPageData

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: DetailSliderAdapter

    private val WRITE_EXT_STORAGE_REQ_CODE = 0

    private fun setPagerAdapter() {
        stories  = viewModel.selectedStoryList.value!!
        adapter = DetailSliderAdapter(fragmentActivity = requireActivity(),itemCount =  stories.size, stories = stories as ArrayList<Story>)

        binding.pager.adapter = adapter
        binding.pager.setCurrentItem(position, false)
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                story = stories[position]
                Log.i("selectedstory", "$story")
                viewModel.getSources(story.storyId, story.events.last().eventId)
                setIconForNotified(viewModel.notifyStoriesLiveData.value)
                updateStateOfArticleOnServer()
                setIconForSaved()

            }
        })
    }

    private fun initViews() {

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnShare.setOnClickListener { share() }

        binding.btnDownload.setOnClickListener { saveStory() }

        binding.btnNotify.setOnClickListener {
            println("12245 notify call saved"+"saved as notifies")
            saveAsNotified() }

        binding.btnNotifyFilled.setOnClickListener {
            println("12245 notify call "+ "removed from notifies")
            removeFromNotified() }

        binding.btnDownloaded.setOnClickListener { deleteSavedStory() }


    }

    private fun updateSavedStoryOnServer(savedStoryIds: ArrayList<Int>, action : () -> Unit){

            viewModel.saveSavedStoryIds(savedStoryIds as ArrayList<Int>)
            viewModel.userSavedStorySaveDataState.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is DataState.Success -> {
                        viewModel.setSavedStoryIds( it.data as ArrayList<Int>)
                        action()
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "onActivityCreated: UserCategoryDatState Error ON SAVE ---> ${it.exception}")
                        if (it.statusCode == 101)
                            viewModel.toast("Cannot save storyId")
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "onActivityCreated: SavedStoryDatState ON SAVE loading")
                    }
                }
            })

    }

    private fun updateNotifyStoryOnServer(notifyStories: List<Payload>, from: String){

        viewModel.saveNotifyStories(notifyStories as ArrayList<Payload>, from)

    }

    private fun updateStateOfArticleOnServer(){

        var notifyStories =  viewModel.notifyStoriesLiveData.value?.toMutableList()
        if (notifyStories == null) {
            notifyStories = mutableListOf()
        }


        val notified = Payload(
            storyId = story.storyId,
            read = ArticleState.READ
        )
        val index = notifyStories.indexOf(notified)
        if( index > -1){

            if(notifyStories[index].read != notified.read){
            notifyStories.set(index, notified)
            notifyStories = notifyStories.toList().distinct().toMutableList()
            println("12245 notify call after updateState is $notifyStories")
            updateNotifyStoryOnServer(notifyStories = notifyStories, "update state" )
            }
        }else{
            println("12245 notify call updateState not done ${index}")
        }
    }

    private fun saveAsNotified() {


       val notified = Payload(
           storyId = story.storyId,
           read = ArticleState.READ
       )
        var notifyStories =  viewModel.notifyStoriesLiveData.value?.toMutableList()

        if (notifyStories == null) {
            notifyStories = mutableListOf()
        }

        if (notifyStories != null) {
            notifyStories.add(notified)
            notifyStories = notifyStories.toList().distinct().toMutableList()
            println("12245 notify call after saved is $notifyStories")
            updateNotifyStoryOnServer(notifyStories = notifyStories , "save")
        }else{
            println("notifyStories list is null")
        }

        Snackbar.make(binding.root, "We'll notify you 😀", Snackbar.LENGTH_SHORT).show()
        setIconForNotified(notifyStories)

    }

    private fun removeFromNotified() {

        val notified = Payload(
            storyId = story.storyId,
            read = ArticleState.READ
        )
        var notifyStories =  viewModel.notifyStoriesLiveData.value?.toMutableList()

        if (notifyStories == null) {
            notifyStories = mutableListOf()
        }

        if (notifyStories != null) {
            notifyStories.remove(notified)
            println("12245 notify call after removed is $notifyStories")
            updateNotifyStoryOnServer(notifyStories = notifyStories , "delete")


        }else{
            println("notifyStories list is null")
        }






        Snackbar.make(binding.root, "No notifications will be given", Snackbar.LENGTH_SHORT).show()
        setIconForNotified(notifyStories)

    }
    fun setIconForNotified(it: List<Payload>?){
        if(it != null) {
            if (it.contains(Payload(0, story.storyId))){
                binding.btnNotifyFilled.visibility = View.VISIBLE
            }else{
                binding.btnNotifyFilled.visibility = View.GONE
            }
        }
    }

    fun setIconForSaved(){
        val it = viewModel.savedStoryIdLiveData.value

        if(it != null) {
            if (it.contains(story.storyId)){
                Log.i("12245 saved detail", "true ${story.storyId}")
                binding.btnDownload.visibility = View.INVISIBLE
                binding.btnDownloaded.visibility = View.VISIBLE
            }else{
                Log.i("12245 saved detail", "false ${story.storyId}")
                binding.btnDownload.visibility = View.VISIBLE
                binding.btnDownloaded.visibility = View.INVISIBLE
            }
        }
    }

    private fun saveStory() {

        println("SAVING STORY")

        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events,
            viewCount = story.viewCount
        )

        var savedStoryIds =  viewModel.savedStoryIdLiveData.value?.toMutableList()

        if (savedStoryIds == null) {
            savedStoryIds = mutableListOf()
        }

        if (savedStoryIds != null) {
            savedStoryIds.add(story.storyId)
            println("savedstory list is $savedStoryIds")
            updateSavedStoryOnServer(savedStoryIds = savedStoryIds as ArrayList<Int>) {
                viewModel.saveStory(savedStory)
            }
        }else{
            println("savedstory list is null")
        }


        Snackbar.make(binding.root, "News story saved", Snackbar.LENGTH_SHORT).show()
        binding.btnDownload.visibility = View.INVISIBLE
        binding.btnDownloaded.visibility = View.VISIBLE

    }

    private fun deleteSavedStory() {

        println("DELETING SAVEDSTORY")

        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events,
            viewCount = story.viewCount
        )

        var savedStoryIds =  viewModel.savedStoryIdLiveData.value?.toMutableList()

        if (savedStoryIds == null) {
            savedStoryIds = mutableListOf()
        }

        if (savedStoryIds != null) {
            savedStoryIds.remove(story.storyId)
            println("savedstory list is $savedStoryIds")
            updateSavedStoryOnServer(savedStoryIds = savedStoryIds as ArrayList<Int>) {
                viewModel.deleteSavedStory(savedStory)
            }
        }else{
            println("savedstory list is null")
        }

        binding.btnDownload.visibility = View.VISIBLE
        binding.btnDownloaded.visibility = View.INVISIBLE
        Snackbar.make(binding.root, "News story unsaved", Snackbar.LENGTH_SHORT).show()
    }

    private fun observer() {

        viewModel.saveNewsState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {
                    setIconForSaved()
                    println("NEWS SAVE SUCCESSFUL")
                }
                is DataState.Error -> {
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    println("NEWS SAVING")
                }
            }
        })

    }
    var isPreviousStatusBarLight = true
    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        detailsBottomNavInterface.isBottomNavEnabled(true)

        /*requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.LAYOUT,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);*/

        val window: Window = requireActivity().getWindow()
        val view: View = window.getDecorView()
        WindowInsetsControllerCompat( window, view).isAppearanceLightStatusBars = isPreviousStatusBarLight
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/

        requireActivity().window.statusBarColor = Color.BLACK

        val window: Window = requireActivity().getWindow()
        val view: View = window.getDecorView()
        isPreviousStatusBarLight = WindowInsetsControllerCompat( window, view).isAppearanceLightStatusBars
        WindowInsetsControllerCompat( window, view).isAppearanceLightStatusBars = false


        detailsBottomNavInterface.isBottomNavEnabled(false)

        binding.lifecycleOwner = requireActivity()
        data = viewModel.selectedDetailsPageData

        println("DATA___> $data")

        position = data.position

        stories = viewModel.selectedStoryList.value!!
        if(stories!=null)
        story = stories.get(position)
        viewModel.getSources(story.storyId, story.events.last().eventId)

        setIconForNotified(viewModel.notifyStoriesLiveData.value)
        updateStateOfArticleOnServer()
        setIconForSaved()

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        println("VIEWMODEL: $viewModel")

        initViews()
        setPagerAdapter()
        observer()



    }

    private fun share() {

    when {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            shareImage()
        }
        else -> {
            // You can directly ask for the permission.
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXT_STORAGE_REQ_CODE)
        }
    }

    }

    private fun shareImage() {
       val layout =  binding.pager.getChildAt(0).findViewById<ConstraintLayout>(R.id.constraint_layout)

        val imageUri = layout.drawToBitmap().let { bitmap: Bitmap ->
            saveBitmap(requireActivity(), bitmap)
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.putExtra(Intent.EXTRA_TEXT, "Get more such content by downloading Newsta App\nhttps://play.google.com/store/apps/details?id=com.newsta.android")
        intent.type = "image/jpeg"
        startActivity(Intent.createChooser(intent, "Share the whole pic of news"))
    }

    val DEFAULT_FILENAME = "${System.currentTimeMillis()}.jpeg"

    fun saveBitmap(activity: Activity, bitmap: Bitmap, filename: String = DEFAULT_FILENAME): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val contentResolver = activity.contentResolver

        val imageUri: Uri? = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        return imageUri.also {
            val fileOutputStream = imageUri?.let { contentResolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream?.close()
        }
    }

    override fun getFragmentView(): Int = R.layout.fragment_details

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_EXT_STORAGE_REQ_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    shareImage()
                } else {
                    Snackbar.make(binding.root, "You need to allow us to create image of news to share.", Snackbar.LENGTH_SHORT).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    companion object {
        private lateinit var detailsBottomNavInterface: DetailsBottomNavInterface

        fun setDetailsBottomNavInterface(detailsBottomNavInterface2: DetailsBottomNavInterface) {
            detailsBottomNavInterface = detailsBottomNavInterface2
        }
    }

}
