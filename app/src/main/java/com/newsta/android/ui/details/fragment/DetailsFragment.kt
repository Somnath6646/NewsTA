package com.newsta.android.ui.details.fragment

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.drawToBitmap
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.newsta.android.MainActivity.Companion.isConnectedToNetwork
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentDetailsBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.adapter.*
import com.newsta.android.ui.landing.adapter.ViewPagerAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.squareup.picasso.Picasso
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

    private fun setPagerAdapter(){
        stories  = viewModel.selectedStoryList.value!!
        adapter = DetailSliderAdapter(fragmentActivity = requireActivity(),itemCount =  stories.size, stories = stories as ArrayList<Story>)

        binding.pager.adapter = adapter
        binding.pager.setCurrentItem(position, false)
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                story = stories[position]
                viewModel.getSavedStory(storyId = story.storyId)
                viewModel.getSources(story.storyId, story.events.last().eventId)
            }
        })
    }

    private fun initViews() {


        viewModel.getSavedStory(story.storyId)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnShare.setOnClickListener { shareImage() }

        binding.btnDownload.setOnClickListener { saveStory() }

        binding.btnDownloaded.setOnClickListener { deleteSavedStory() }


    }

    private fun saveStory() {

        println("SAVING STORY")



        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events
        )

        viewModel.saveStory(savedStory)
        Snackbar.make(binding.root, "News story saved", Snackbar.LENGTH_SHORT).show()
        binding.btnDownload.visibility = View.INVISIBLE

    }

    private fun deleteSavedStory() {

        println("DELETING SAVEDSTORY")

        val savedStory = SavedStory(
            storyId = story.storyId,
            category = story.category,
            updatedAt = story.updatedAt,
            events = story.events
        )

        viewModel.deleteSavedStory(savedStory)
        binding.btnDownload.visibility = View.VISIBLE
        Snackbar.make(binding.root, "News story unsaved", Snackbar.LENGTH_SHORT).show()
    }



    private fun observer() {


        viewModel.saveNewsState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {
                    binding.btnDownload.visibility = View.INVISIBLE
                    println("NEWS SAVE SUCCESSFUL")
                }
                is DataState.Error -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVING")
                }
            }
        })

        viewModel.checkSavedStoryState.observe(viewLifecycleOwner, Observer {

            when (it) {
                is DataState.Success<SavedStory?> -> {
                    val savedStory = it.data
                    if (it.data != null) {
                        binding.btnDownload.visibility = View.INVISIBLE
                        println("NEWS WAS SAVED")
                    }
                }
                is DataState.Error -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVE ERROR")
                }
                is DataState.Loading -> {
                    binding.btnDownload.visibility = View.VISIBLE
                    println("NEWS SAVING")
                }
            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        data = requireArguments().getParcelable<DetailsPageData>("data")!!

        position = data.position

        stories = viewModel.selectedStoryList.value!!
        if(stories!=null)
        story = stories.get(position)

        binding.lifecycleOwner = requireActivity()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        println("VIEWMODEL: $viewModel")

        initViews()
        setPagerAdapter()
        observer()


    }

    private fun shareImage() {
       val layout =  binding.pager.getChildAt(0).findViewById<ConstraintLayout>(R.id.constraint_layout)

        val imageUri = layout.drawToBitmap().let { bitmap: Bitmap ->
            saveBitmap(requireActivity(), bitmap)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)

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

}
