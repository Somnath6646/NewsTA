package com.newsta.android.ui.notify

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.newsta.android.databinding.FragmentNotifyBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.remote.data.Payload
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.notify.adapter.NotifyStoriesAdapter
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.MyItemKeyProvider
import com.newsta.android.utils.NotifyStoryItemDetailsLookup
import com.newsta.android.utils.SavedStoryItemDetailsLookup
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel
import kotlinx.android.synthetic.main.fragment_categories.*


class NotifyFragment : BaseFragment<FragmentNotifyBinding>(), OnDataSetChangedListener {

    private val viewModel: NewsViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null

    private lateinit var adapter: NotifyStoriesAdapter
    private var notifyStories = ArrayList<SavedStory>()

    private fun setUpAdapter() {
        adapter = NotifyStoriesAdapter({ position: Int-> openDetails(position) })
        adapter.setDataSetChangeListener(this)
        binding.recyclerView.adapter = adapter
        initTracker()
        adapter.tracker = tracker
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initTracker(){
        tracker = SelectionTracker.Builder<Long>(

            "mySelection143",
            binding.recyclerView,
            MyItemKeyProvider(binding.recyclerView),
            NotifyStoryItemDetailsLookup(binding.recyclerView),
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
                notifyStories.get(it.toInt())
            }

            showDeleteDialog(list)

            disableDeleteBtn()
            tracker?.clearSelection()
        }
    }

    private fun openDetails(position: Int) {
        val detailsPageData = DetailsPageData(position)
        val bundle = bundleOf("data" to detailsPageData)
        updateStateOfArticleOnServer(position, notifyStories.get(position), ArticleState.READ)
        findNavController().navigate(R.id.action_notifyFragment_to_detailsFragment, bundle)
    }

    private fun showDeleteDialog(savedStory: List<SavedStory>): Boolean {

        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(LayoutInflater.from(requireContext()), R.layout.logout_dialog, null, false)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.message.text = "Are you sure you want to remove this story from notify list?"
        dialogBinding.buttonTextAction.text = "Remove"

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnAction.setOnClickListener {
            deleteFromServerandLocaldb(savedStory){

            }

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        return true

    }

    private fun updateNotifyStoryOnServer(state: Int, list: List<SavedStory>, actionWithListBeforeUpload: (MutableList<Payload>, ArrayList<Payload>) -> MutableList<Payload>){


        var notifyStoryIds =  viewModel.notifyStoriesLiveData.value?.toMutableList()

        if (notifyStoryIds == null) {
            notifyStoryIds = mutableListOf()
        }

        if (notifyStoryIds != null) {
            val payloadList = arrayListOf<Payload>()
            list.forEach {
                payloadList.add(Payload(storyId = it.storyId, read = state))
            }
            notifyStoryIds = actionWithListBeforeUpload(notifyStoryIds, payloadList)
            println("notifystory list is $notifyStoryIds")
        }else{
            println("notifystory list is null")
        }

        viewModel.saveNotifyStories(notifyStoryIds as ArrayList<Payload>)

    }

    private fun updateStateOfArticleOnServer(position: Int, savedStory: SavedStory, state: Int){
        updateNotifyStoryOnServer(state, listOf(savedStory)) { notifyStoryIds, payloadList ->
            notifyStoryIds.set(position, payloadList[0])
            notifyStoryIds
        }
    }

    private fun deleteFromServerandLocaldb(toDeleteList: List<SavedStory>, action: () -> Unit){
        updateNotifyStoryOnServer(ArticleState.READ, toDeleteList, { notifyStoryIds, payloadList ->
            notifyStoryIds.removeAll(payloadList)
            notifyStoryIds
        })
    }

    private fun observer(){
        viewModel.notifyStoriesLiveData.observe(viewLifecycleOwner, Observer {payloads ->
            if(payloads != null) {
                var payloads2 = payloads
                val list = arrayListOf<Int>()
                payloads2 = payloads2.sortedBy {
                    it.storyId
                }
                payloads2.forEach {
                    list.add(it.storyId)
                }
                    viewModel.getStoriesByIds(list) {

                        notifyStories = it
                        adapter.addAll(notifyStories, ArrayList(payloads2))
                    }


            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observer()

        setUpAdapter()


        binding.back.setOnClickListener {
            findNavController().popBackStack() }

    }

    override fun getFragmentView(): Int {
        return R.layout.fragment_notify
    }

    override fun onDataSetChange(stories: List<Story>) {
        viewModel.setSelectedStoryList(stories)
    }

}