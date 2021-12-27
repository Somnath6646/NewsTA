package com.newsta.android.ui.saved.fragment

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
import android.widget.Toast
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
import com.facebook.login.LoginManager
import com.newsta.android.MainActivity
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.details.DetailsActivity
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.SavedStoryItemDetailsLookup
import com.newsta.android.utils.MyItemKeyProvider
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story

class SavedStoriesFragment : BaseFragment<FragmentSavedStoriesBinding>(), OnDataSetChangedListener {

    private val viewModel: NewsViewModel by activityViewModels()
    private var tracker: SelectionTracker<Long>? = null

    private lateinit var adapter: SavedStoryAdapter
    private var savedStories = ArrayList<SavedStory>()

    private fun setUpAdapter() {
        adapter = SavedStoryAdapter({ position: Int-> openDetails(position) }, {  true })
        adapter.setDataSetChangeListener(this)
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
            SavedStoryItemDetailsLookup(binding.recyclerView),
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

    private fun openDetails(position: Int) {
        val data = viewModel.selectedStoryList.value?.get(position)?.events?.last()?.let { DetailsPageData(position, it.eventId) }
        val bundle = bundleOf("data" to data)
        if (data != null) {
            viewModel.selectedDetailsPageData = data
            findNavController().navigate(R.id.action_savedStoriesFragment_to_detailsFragment2, bundle)
        }else{
            viewModel.debugToast("selected story list is null")
        }
    }

    private fun showDeleteDialog(savedStory: List<SavedStory>): Boolean {

        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<LogoutDialogBinding>(LayoutInflater.from(requireContext()), R.layout.logout_dialog, null, false)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.message.text = "Are you sure you want to delete this story?"
        dialogBinding.buttonTextAction.text = "Delete"

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnAction.setOnClickListener {
           deleteFromServerandLocaldb(savedStory){
               viewModel.deleteSavedStory(savedStory)
           }

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        return true

    }

    private fun updateStoryOnServer(savedStoryIds: ArrayList<Int>, action : () -> Unit){

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

    private fun deleteFromServerandLocaldb(toDeleteList: List<SavedStory>, action: () -> Unit){
        var savedStoryIds =  viewModel.savedStoryIdLiveData.value?.toMutableList()

        if (savedStoryIds == null) {
            savedStoryIds = mutableListOf()
        }

        if (savedStoryIds != null) {
            val idList = arrayListOf<Int>()
            toDeleteList.forEach {
                idList.add(it.storyId)
            }
            savedStoryIds.removeAll(idList)
            println("savedstory list is $savedStoryIds")
            updateStoryOnServer(savedStoryIds = savedStoryIds as ArrayList<Int>) {
               action()
            }
        }else{
            println("savedstory list is null")
        }
    }

    private fun observer() {

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let { message ->
                if (!message.isNullOrBlank())
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })



        viewModel.savedStoriesList.observe(viewLifecycleOwner, Observer {

                    savedStories = ArrayList(it)
                    adapter.addAll(savedStories)

        })

        viewModel.savedStoriesDeleteState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<SavedStory> -> {
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

        viewModel.logoutDataState.observe(requireActivity(), Observer {
            it.getContentIfNotHandled().let {
                when (it) {
                    is DataState.Success -> {
                        Log.i("TAG", "Sucess logout ")

                        LoginManager.getInstance().logOut();
                        viewModel.clearAllData()
                        /*val action =
                            LandingFragmentDirections.actionMainLandingFragmentToNavGraph()
                        findNavController().navigate(action)*/
                        val context = requireActivity().applicationContext

                        if(context!= null) {
                            requireActivity().startActivity(Intent(context, MainActivity::class.java))
                            requireActivity().finish()

                        }

                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "Loading logout ")
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "Error logout ")
                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(
                            LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false
                        )
                        dialog.setContentView(dialogBinding.root)

                        println("Abhi hai $dialogBinding")

                        dialogBinding.message.text = "${it.exception}"
                        dialogBinding.buttonText.text = "Try Again"
                        dialogBinding.button.setOnClickListener { v ->
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }

                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                        dialog.show()
                    }
                    else -> {

                    }
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

        binding.sideNavDrawer4.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer4.menu.getItem(0).subMenu.getItem(1).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        modeSwitch.isChecked = NewstaApp.isDarkMode

        binding.sideNavDrawer4.setNavigationItemSelectedListener {
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
                    findNavController().navigate(R.id.action_savedStoriesFragment_to_settingsFragment2)
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
                    /*val url = "https://thirsty-heyrovsky-b69ef3.netlify.app/privacy"
                    val webpage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(Intent.createChooser(intent, "Contact us - contact@newsta.in"))
                    true
                    findNavController().navigate(R.id.action_landingFragment_to_privacyFragment)
                    closeNavigationDrawer()*/
                    val url = "https://thirsty-heyrovsky-b69ef3.netlify.app/privacy"
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
                    val url = "https://thirsty-heyrovsky-b69ef3.netlify.app/feedback"
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


    override fun getFragmentView(): Int = R.layout.fragment_saved_stories
    override fun onDataSetChange(stories: List<Story>) {
       viewModel.setSelectedStoryList(stories)
    }

}
