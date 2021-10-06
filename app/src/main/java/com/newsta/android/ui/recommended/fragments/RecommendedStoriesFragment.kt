package com.newsta.android.ui.recommended.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsta.android.MainActivity
import com.newsta.android.NewstaApp
import com.newsta.android.databinding.FragmentSavedStoriesBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.R
import com.newsta.android.databinding.FragmentRecommendedNewsBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.landing.adapter.NewsAdapter
import com.newsta.android.ui.recommended.adapter.RecommendedStoriesAdapter
import com.newsta.android.ui.saved.adapter.SavedStoryAdapter
import com.newsta.android.utils.helpers.OnDataSetChangedListener
import com.newsta.android.utils.models.DetailsPageData
import com.newsta.android.utils.models.RecommendedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.viewmodels.NewsViewModel

class RecommendedStoriesFragment: BaseFragment<FragmentRecommendedNewsBinding>(), OnDataSetChangedListener {
    override fun getFragmentView(): Int  = R.layout.fragment_recommended_news

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: RecommendedStoriesAdapter

    private fun setUpAdapter() {

        adapter = RecommendedStoriesAdapter { position: Int -> openDetails(position) }
        adapter.setDataSetChangeListener(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun openDetails(position: Int) {
        val data = DetailsPageData(position)
        val bundle = bundleOf("data" to data)
        viewModel.selectedDetailsPageData = data
        findNavController().navigate(R.id.action_recommendedStoriesFragment_to_detailsFragment2, bundle)



    }

    private fun initViews() {

        binding.refreshLayout.setOnRefreshListener {
                viewModel.getRecommendedStories()
                binding.refreshLayout.isRefreshing = false
            }
        }

    override fun onDataSetChange(stories: List<Story>) {
        viewModel.setSelectedStoryList(stories)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        setUpNavigationDrawer()
        setUpAdapter()
        initViews()
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.recommendedStoriesLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null){
                adapter.addAll(ArrayList(it.reversed()))
            }
        })

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setUpNavigationDrawer() {

        binding.navDrawer.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.sideNavDrawer2.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer2.menu.getItem(0).subMenu.getItem(1).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        modeSwitch.isChecked = NewstaApp.isDarkMode

        binding.sideNavDrawer2.setNavigationItemSelectedListener {
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
                    findNavController().navigate(R.id.action_recommendedStoriesFragment_to_settingsFragment2)
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

}