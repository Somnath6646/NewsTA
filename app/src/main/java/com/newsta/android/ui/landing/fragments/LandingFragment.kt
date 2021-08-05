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
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.facebook.login.LoginManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.newsta.android.BuildConfig
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
import com.newsta.android.utils.models.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var categories: ArrayList<Category> = ArrayList()
    private var userCategories: ArrayList<Category> = ArrayList()
    private lateinit var userPreferences: UserPreferences
    private var category = 0
    private lateinit var adapter: ViewPagerAdapter
    private var isAppJustOpened = true

    private fun setUpAdapter(categories: ArrayList<Category>) {

        println("SETTING UP TAB LAYOUT")

        adapter = ViewPagerAdapter(
            fragmentActivity = requireActivity(),
            itemCount = categories.size,
            categories = categories
        )

        println("PAGER ADAPTER ---> $adapter")

        binding.pager.adapter = adapter
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        if (isAppJustOpened) {
            println("APP JUST OPENED")
            isAppJustOpened = false
            binding.pager.setCurrentItem(0, false)
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        }
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

    private fun getUserCategories() {

        viewModel.getUserCategories()
        viewModel.userPreferencesDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    Log.i("TAG", "UserCategoryDatState Success ---> ${it.data}")
                    userPreferences = it.data
                    println("USER CATEGORIES ---> $userCategories")
//                    val userCategories = ArrayList<Category>()
//                    StoriesDisplayFragment.categoryState = userCategories[0].categoryId
                    userCategories.clear()
                    userPreferences.categories?.forEach { categoryId ->
                        categories.forEach { category ->
                            if(categoryId == category.categoryId) {
                                println("${category.categoryId} ---> Present")
                                userCategories.add(Category(category.category, category.categoryId))
                            }
                        }
                    }
                    println("USER CATEGORIES ---> $userCategories")
                    println("CATEGORIES ---> $categories")
                    if (categories != userCategories) {
//                        categories = userCategories
                        isAppJustOpened = true
                        setUpTabLayout(categories = ArrayList()).let {
                            println("USER CATEGORIES ---> $userCategories")
                            if (userCategories.size > 0)
                                setUpTabLayout(categories = userCategories)
                            else
                                setUpTabLayout(categories = categories)
                        }
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

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            println("RETURNED")
        }

        binding.viewModel = viewModel

        println("Access token: ${NewstaApp.access_token}")

        binding.lifecycleOwner = requireActivity()

        println("VIEWMODEL: $viewModel")

        binding.back.setOnClickListener {

            if (binding.searchLayout.visibility == View.VISIBLE) {
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
            when (it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                    var newCategories = it.data as ArrayList<Category>
                    if (newCategories == categories) {
                        println("CATEGORIES SAME")
                        if (!NewstaApp.has_changed_preferences!! && userCategories.size == 0) {
                            println("CATEGORY SE SET HUA NOT CHANGED")
                            setUpTabLayout(categories = categories)
                        }
                    } else {
                        if (!NewstaApp.has_changed_preferences!!) {
                            println("CATEGORIES DIFFERENT")
                            categories = newCategories
                            setUpTabLayout(categories = categories)
                        }
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
            when (it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Success DB")
                    if (isAppJustOpened)
                        viewModel.getCategories()
                    if (categories.size <= 0) {
                        categories = it.data as ArrayList<Category>
                        println("NOT CHANGED USER PREFERENCES ---> $categories")
                        if (NewstaApp.has_changed_preferences!!) {
                            println("CHANGED USER PREFERENCES")
                            getUserCategories()
//                        setUpTabLayout(categories = categories)
                        } else {
                            println("NOT CHANGED USER PREFERENCES ---> $categories")
                            if(binding.pager.childCount <= 0)
                                setUpTabLayout(categories = categories)
                        }
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

        viewModel.logoutDataState.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                when (it) {
                    is DataState.Success -> {
                        Log.i("TAG", "Sucess logout ")

                        viewModel.clearAllData()
//                        Toast.makeText(requireContext(), "Cleared!!!", Toast.LENGTH_SHORT).show()
                        val action =
                            LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
                        findNavController().navigate(action)
                        LoginManager.getInstance().logOut();
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

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.debugToast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null){

                    if(BuildConfig.DEBUG){
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        setUpNavigationDrawer()

    }

    private fun checkIfUnauthorized(data: DataState.Error) {
        if (data.statusCode == NewstaApp.UNAUTHORIZED_STATUS_CODE) {
            viewModel.toast("We've encountered an issue at our end please log in again to continue. We're sorry for the inconvenience caused.")
            viewModel.clearAllData()
            val action =
                LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
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
                        if (category == 0) {
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
