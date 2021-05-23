package com.newsta.android.ui.saved.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.MyItemDetailsLookup
import com.newsta.android.utils.MyItemKeyProvider
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story

class SavedStoriesFragment : BaseFragment<FragmentSavedStoriesBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null

    private lateinit var adapter: SavedStoryAdapter
    private var savedStories = ArrayList<SavedStory>()

    private fun setUpAdapter() {

        adapter = SavedStoryAdapter({ savedStory: SavedStory -> openDetails(savedStory) }, { savedStory: SavedStory -> true })
        binding.recyclerView.adapter = adapter
        initTracker()
        adapter.tracker = tracker
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }


    private fun initTracker(){
        tracker = SelectionTracker.Builder<Long>(

            "mySelection123",
            binding.recyclerView,
            MyItemKeyProvider(binding.recyclerView),
            MyItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()

        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker?.selection!!.size()
                    if (items > 1) {
                        showDeleteBtn(tracker?.selection!!)
                    }else if(items == 1){
                        animateBookmarkButton(tracker?.selection!!)
                    }
                    else{
                        disableDeleteBtn()
                    }
                }
            })
    }

    private fun animateBookmarkButton(selection: Selection<Long>) {
        if(binding.deleteCardContainer.visibility == View.GONE) {

            binding.deleteCardContainer.scaleX = 0f
            binding.deleteCardContainer.scaleY = 0f
            binding.deleteCardContainer.apply {
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            binding.deleteCardContainer.visibility = View.VISIBLE

                        }
                    })
            }
        }
        showDeleteBtn(selection)
    }

    private fun disableDeleteBtn() {
        binding.deleteCardContainer.visibility = View.GONE
    }

    private fun showDeleteBtn(selection: Selection<Long>) {

        binding.deleteCardContainer.visibility = View.VISIBLE
        binding.deleteNumberText.text = selection.size().toString()
        binding.deleteCardContainer.setOnClickListener {
            val list = selection.map {
              savedStories.get(it.toInt())
            }

            showDeleteDialog(list)

            disableDeleteBtn()
            tracker?.clearSelection()
        }
    }

    private fun openDetails(savedStory: SavedStory) {
        val story = Story(
            category = savedStory.category,
            updatedAt = savedStory.updatedAt,
            events = savedStory.events,
            storyId = savedStory.storyId)
        val detailsPageData = DetailsPageData(
            story = story,
            eventId = story.events.last().eventId
        )
        val bundle = bundleOf("data" to detailsPageData)
        findNavController().navigate(R.id.action_savedStoriesFragment_to_detailsFragment, bundle)
    }

    private fun showDeleteDialog(savedStory: List<SavedStory>): Boolean {

        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(LayoutInflater.from(requireContext()), R.layout.logout_dialog, null, false)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.message.text = "Are you sure you want to delete this story?"
        dialogBinding.buttonTextAction.text = "Delete"

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnAction.setOnClickListener {
            viewModel.deleteSavedStory(savedStory)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        return true

    }

    private fun observer() {

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let { message ->
                if (!message.isNullOrBlank())
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.savedStoriesState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<SavedStory>> -> {
                    savedStories = ArrayList(it.data)
                    adapter.addAll(savedStories)
                }
                is DataState.Error -> {
                    println("NEWS SAVED FETCH ERROR")
                }
                is DataState.Loading -> {
                    println("NEWS SAVED LOADING")
                }
            }
        })

        viewModel.savedStoriesDeleteState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {

                    val story = it.data
                    viewModel.getSavedStories()

                }
                is DataState.Error -> {
                    println("NEWS DELETE ERROR")
                }
                is DataState.Loading -> {
                    println("NEWS DELETING")
                }
            }
        })

    }

    private fun toast(message: String) = viewModel.toast(message)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observer()

        setUpAdapter()

        viewModel.getSavedStories()

        binding.back.setOnClickListener { findNavController().popBackStack() }

    }

    override fun getFragmentView(): Int = R.layout.fragment_saved_stories

}
