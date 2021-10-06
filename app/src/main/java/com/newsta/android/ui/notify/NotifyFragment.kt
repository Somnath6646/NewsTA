package com.newsta.android.ui.notify

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentNotifyBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.remote.data.Payload
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.DetailsActivity
import com.newsta.android.ui.notify.adapter.NotifyStoriesAdapter
import com.newsta.android.utils.MyItemKeyProvider
import com.newsta.android.utils.NotifyStoryItemDetailsLookup
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel


class NotifyFragment : BaseFragment<FragmentNotifyBinding>(), OnDataSetChangedListener {

    private val viewModel: NewsViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null

    private lateinit var adapter: NotifyStoriesAdapter


    private fun setUpAdapter() {
        adapter = NotifyStoriesAdapter { storyId: Int ->
            viewModel.notifyStoriesLiveData.value?.let {it1 ->

                openDetails(

                    it1.indexOf(Payload(0, storyId))
                )
            }
        }
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
                viewModel.notifyStories.get(it.toInt())
            }

            showDeleteDialog(list)

            disableDeleteBtn()
            tracker?.clearSelection()
        }
    }

    private fun openDetails(position: Int) {
        val data = DetailsPageData(position)
        val bundle = bundleOf("data" to data)
        viewModel.selectedDetailsPageData = data
        findNavController().navigate(R.id.action_notifyFragment_to_detailsFragment2, bundle)
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

        viewModel.saveNotifyStories(notifyStoryIds as ArrayList<Payload>, "add stories updateNotifyStoryOnServer")

    }

    private fun updateStateOfArticleOnServer(position: Int, savedStory: SavedStory, state: Int){
        updateNotifyStoryOnServer(state, listOf(savedStory)) { notifyStoryIds, payloadList ->
            Log.i("12246 notifyStoryIds", "$notifyStoryIds")
            notifyStoryIds.set(notifyStoryIds.indexOf(Payload(0, savedStory.storyId)), payloadList[0])
            notifyStoryIds
        }
    }

    private fun deleteFromServerandLocaldb(toDeleteList: List<SavedStory>, action: () -> Unit){
        updateNotifyStoryOnServer(ArticleState.READ, toDeleteList) { notifyStoryIds, payloadList ->
            notifyStoryIds.removeAll(payloadList)
            notifyStoryIds
        }
    }

    private fun observer(){
        viewModel.notifyStoriesLiveData.observe(requireActivity(), Observer {payloads ->
           if(payloads != null) {

                var list = arrayListOf<Int>()
                payloads.forEach {
                    list.add(it.storyId)
                }
               println("12245 notify ${viewModel.notifyStories != list} notifystories ${viewModel.notifyStories} \n payloads ${payloads}")


                if(!list.isNullOrEmpty())
                list = ArrayList(list.distinct())
                println("12245 notify id list to fetch ${viewModel.notifyStories != payloads}"+ "$list")

                    viewModel.getStoriesByIds(list) {

                        viewModel.notifyStories = it
                        adapter.addAll(viewModel.notifyStories, ArrayList(payloads))
                    }


            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observer()

        setUpAdapter()

        setUpNavigationDrawer()

    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun setUpNavigationDrawer() {

        binding.sidenav.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.sideNavDrawer3.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer3.menu.getItem(0).subMenu.getItem(1).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        modeSwitch.isChecked = NewstaApp.isDarkMode

        binding.sideNavDrawer3.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.log_out -> {
                    val dialog = Dialog(requireContext())
                    val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(
                        LayoutInflater.from(requireContext()), R.layout.logout_dialog, null, false
                    )
                    dialog.setContentView(dialogBinding.root)

                    println("Abhi hai $dialogBinding")

                    dialogBinding.btnCancel.setOnClickListener { v ->
                        dialog.dismiss()
                        closeNavigationDrawer()
                    }

                    dialogBinding.btnAction.setOnClickListener {
                        viewModel.logOut()
                        dialog.dismiss()
                        closeNavigationDrawer()
                    }

                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.show()
                    true
                }


                R.id.settings -> {
                    findNavController().navigate(R.id.action_notifyFragment_to_settingsFragment2)
                    closeNavigationDrawer()
                    true
                }

                R.id.rate_us -> {
                    val url = "market://details?id=${context?.packageName}"
                    val webpage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(Intent.createChooser(intent, "Rate us on Google Play Store"))
                    true
                }

                R.id.share_app -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(Intent.EXTRA_TEXT, "Download the Newsta app to get Latest, Short, Factual and Connected news stories.\nhttps://play.google.com/store/apps/details?id=com.newsta.android")
                    intent.type = "text/*"
                    startActivity(Intent.createChooser(intent, "Share our app"))
                    true
                }

                R.id.privacy_policy -> {
                    /*val url = "http://www.newsta.in/privacy"
                    val webpage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(Intent.createChooser(intent, "Contact us - contact@newsta.in"))
                    true
                    findNavController().navigate(R.id.action_landingFragment_to_privacyFragment)
                    closeNavigationDrawer()*/
                    val url = "http://www.newsta.in/privacy"
                    val webpage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(Intent.createChooser(intent, "Rate us on Google Play Store"))
                    true
                }

                R.id.contact -> {
                    /*val addresses = arrayOf("contact@newsta.in")
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:") // only email apps should handle this
                        putExtra(Intent.EXTRA_EMAIL, addresses)
                    }
                    startActivity(intent)
                    true
                    findNavController().navigate(R.id.action_landingFragment_to_feedBackFragment)
                    closeNavigationDrawer()*/
                    val url = "http://www.newsta.in/feedback"
                    val webpage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(Intent.createChooser(intent, "Rate us on Google Play Store"))
                    true
                }

                else -> {
                    false
                }
            }
        }

        modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                if(!NewstaApp.isDarkMode)
                    viewModel.setIsDarkMode(true)
            }
            else {
                if(NewstaApp.isDarkMode)
                    viewModel.setIsDarkMode(false)
            }
        }
    }

    private fun closeNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }


    override fun getFragmentView(): Int {
        return R.layout.fragment_notify
    }

    override fun onDataSetChange(stories: List<Story>) {
        viewModel.setSelectedStoryList(stories)
    }

}