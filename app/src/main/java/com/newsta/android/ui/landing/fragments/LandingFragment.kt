package com.newsta.android.ui.landing.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.facebook.login.LoginManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.newsta.android.MainActivity
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.ViewPagerAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.utils.models.Category
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.Story
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Error
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var categories: ArrayList<Category> = ArrayList()
    private var category = 0
    private lateinit var adapter: ViewPagerAdapter

    private fun setUpAdapter(categories: ArrayList<Category>) {

        adapter = ViewPagerAdapter(
            fragmentActivity = requireActivity(),
            itemCount = categories.size,
            categories = categories
        )

        binding.pager.adapter = adapter
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.pager.setCurrentItem(0, true)
    }

    private fun setUpNavigationDrawer() {

        binding.navDrawer.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.sideNavDrawer.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer.menu.getItem(0).subMenu.getItem(2).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        binding.sideNavDrawer.setNavigationItemSelectedListener {
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

                R.id.saved -> {
                    findNavController().navigate(R.id.action_landingFragment_to_savedStoriesFragment)
                    closeNavigationDrawer()
                    true
                }

                R.id.settings -> {
                    findNavController().navigate(R.id.action_landingFragment_to_settingsFragment)
                    closeNavigationDrawer()
                    true
                }

                else -> {
                    false
                }
            }
        }

        modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(savedInstanceState != null) {
            println("RETURNED")
        }

        binding.viewModel = viewModel

        println("Access token: ${NewstaApp.access_token}")

        binding.lifecycleOwner = requireActivity()

        println("VIEWMODEL: $viewModel")

        binding.back.setOnClickListener {

            if(binding.searchLayout.visibility == View.VISIBLE) {
                binding.navDrawer.visibility = View.VISIBLE
                /*binding.appName.visibility = View.VISIBLE*/
                binding.searchLayout.visibility = View.GONE
                binding.back.visibility = View.GONE
            }
        }

        binding.search.setOnClickListener {
            val action = LandingFragmentDirections.actionLandingFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        viewModel.categoryDataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                    var newCategories = it.data as ArrayList<Category>
                    if(newCategories == categories) {
                        println("CATEGORIES SAME")
                    } else {
                        println("CATEGORIES DIFFERENT")
                        categories = newCategories
                        setUpTabLayout(categories = categories)
                    }
                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Error")
                    checkIfUnauthorized(it)
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                }
            }
        })

        viewModel.dbCategoryDataState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                    categories = it.data as ArrayList<Category>
                    setUpTabLayout(categories = categories)
                    getNewsFromDatabase()
                    viewModel.getCategories()
                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Error")
                    checkIfUnauthorized(it)
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                }
            }
        })

        viewModel.logoutDataState.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                when(it){
                    is DataState.Success -> {
                        Log.i("TAG", "Sucess logout ")

                        viewModel.clearAllData()
//                        Toast.makeText(requireContext(), "Cleared!!!", Toast.LENGTH_SHORT).show()
                        val action = LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
                        findNavController().navigate(action)
                        LoginManager.getInstance().logOut();
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "Loading logout ")
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "Error logout ")
                        val dialog = Dialog(requireContext())
                        val dialogBinding = DataBindingUtil.inflate<AuthDialogBinding>(LayoutInflater.from(requireContext()), R.layout.auth_dialog, null, false)
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

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if(it != null)
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        setUpNavigationDrawer()

    }

    private fun getNewsFromDatabase() {
        /*viewModel.dbNewsDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<List<Story>?> -> {
                    Log.i("newsDataState", " success")
//                    binding.refreshLayout.isRefreshing = false
                    viewModel.changeDatabaseState(isDatabaseEmpty = false)
                    NewsViewModel.stories = ArrayList(it.data)
                    val filteredStories =
                        NewsViewModel.stories.filter { story: Story -> story.category == 0 }
                    if (filteredStories.isNullOrEmpty()) {
                        NewstaApp.is_database_empty = true
                        viewModel.changeDatabaseState(true)
                        viewModel.getNewsOnInit()
                        NewstaApp.setIsDatabaseEmpty(true)
                    }
                    println("FilteredStories  $filteredStories")

                    val stories = ArrayList<Story>(filteredStories)
                    stories.sortByDescending {
                            story ->  story.updatedAt
                    }
//                    adapter.addAll(stories)
                }
                is DataState.Error -> {
                    Log.i("newsDataState", " errror ${it.exception}")
//                    binding.refreshLayout.isRefreshing = false
                }
                is DataState.Loading -> {
                    Log.i("newsDataState", " loding")
//                    binding.refreshLayout.isRefreshing = true
                }
                is DataState.Extra<List<Story>?> -> {
                    try {
                        if (!it.data.isNullOrEmpty()) {
                            MainActivity.maxStory = it.data.first()
                            MainActivity.minStory = it.data.last()
                            MainActivity.extras = ArrayList(it.data)
                        }
                    } catch (e: Exception) {
//                        Toast.makeText(requireContext(), "Min Max error", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    Log.i(
                        "newsDataState",
                        " EXTRA MAX ${MainActivity.maxStory.storyId} ${MainActivity.maxStory.updatedAt} ${MainActivity.maxStory.category} ${MainActivity.maxStory.events}"
                    )
                    Log.i(
                        "newsDataState",
                        " EXTRA MIN ${MainActivity.maxStory.storyId} ${MainActivity.maxStory.updatedAt} ${MainActivity.maxStory.category} ${MainActivity.maxStory.events}"
                    )
//                    viewModel.getAllNews(maxStory.storyId, maxStory.updatedAt)
                }
            }
        })*/
    }

    private fun checkIfUnauthorized(data: DataState.Error) {
        if (data.statusCode == NewstaApp.UNAUTHORIZED_STATUS_CODE) {
            viewModel.toast("We've encountered an issue at our end please log in again to continue. We're sorry for the inconvenience caused.")
            viewModel.clearAllData()
            val action = LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
            findNavController().navigate(action)
            LoginManager.getInstance().logOut();
        } else
            Toast.makeText(requireContext(), data.exception, Toast.LENGTH_SHORT).show()
    }

    private fun setUpTabLayout(categories: ArrayList<Category>) {

        setUpAdapter(categories)

        TabLayoutMediator(binding.tabLayout, binding.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.customView = when (position) {
                    0 -> {
                        if(category == 0) {
                            addCustomView(
                                categories[position].category.capitalize(Locale.ROOT), 16f,
                                Color.WHITE
                            )
                        } else {
                            addCustomView(categories[position].category.capitalize(Locale.ROOT))
                        }
                    }
                    else -> {
                        if (position < categories.size)
                            addCustomView(categories[position].category.capitalize(Locale.ROOT))
                        else
                            return@TabConfigurationStrategy
                    }
                }
            }).attach()

        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {

                category = tab!!.position

                println("TAB POSITION: $category")


                binding.tabLayout.setScrollPosition(category, 0f, true)
                binding.pager.currentItem = category

                tab.view.children.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.textSize = 16f
                                view2.setTextColor(Color.WHITE)
                            }
                        }
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

                tab?.view?.children?.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorSecondary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.setTextSize(14f)
                                view2.setTextColor(resources.getColor(R.color.colorText))
                            }
                        }
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                category = tab!!.position

                println("TAB POSITION: $category")

                binding.tabLayout.setScrollPosition(category, 0f, true)
                binding.pager.currentItem = category

                tab.view.children.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.textSize = 16f
                                view2.setTextColor(Color.WHITE)
                            }
                        }
                    }
                }

            }

        })

    }

    @SuppressLint("ResourceAsColor")
    private fun addCustomView(
        title: String,
        size: Float = 14f,
        color: Int = R.color.colorText
    ): View {
        val view = layoutInflater.inflate(R.layout.item_tab, binding.tabLayout, false)
        val titleTextView = view.findViewById<TextView>(R.id.title)
        titleTextView.apply {
            text = title


            if (color == Color.WHITE) {
                (view.findViewById<CardView>(R.id.cardView)).setCardBackgroundColor(
                    resources.getColor(
                        R.color.colorPrimary
                    )
                )
                setTextColor(color)
            } else {
                setTextColor(resources.getColor(color))
            }

            textSize = size
        }

        return view
    }

    private fun closeNavigationDrawer() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun getFragmentView(): Int = R.layout.fragment_landing

    override fun onDestroy() {
        println("DESTROYED*****")
        super.onDestroy()
    }

    override fun onResume() {
        print("RESUMED")
        val tab = binding.tabLayout.getTabAt(category)
        tab?.select()
        super.onResume()
    }

}
