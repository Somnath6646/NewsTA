package com.newsta.android.ui.saved.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.viewmodel.NewsViewModel
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import kotlinx.android.synthetic.main.logout_dialog.*

class SavedStoriesFragment : BaseFragment<FragmentSavedStoriesBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: SavedStoryAdapter
    private var savedStories = ArrayList<SavedStory>()

    private fun setUpAdapter() {

        adapter = SavedStoryAdapter({ savedStory: SavedStory -> openDetails(savedStory) }, { savedStory: SavedStory -> showDeleteDialog(savedStory) })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(savedStory: SavedStory) {
        val story = Story(
            category = savedStory.category,
            updatedAt = savedStory.updatedAt,
            events = savedStory.events,
            storyId = savedStory.storyId)
        val bundle = bundleOf("data" to story)
        findNavController().navigate(R.id.action_savedStoriesFragment_to_detailsFragment, bundle)
    }

    private fun showDeleteDialog(savedStory: SavedStory): Boolean {

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
