package com.newsta.android.ui.landing.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.newsta.android.BuildConfig
import com.newsta.android.MainActivity.Companion.categoryState
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.AuthDialogBinding
import com.newsta.android.databinding.FragmentLandingBinding
import com.newsta.android.databinding.LogoutDialogBinding
import com.newsta.android.remote.data.ArticleState
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.landing.adapter.ViewPagerAdapter
import com.newsta.android.viewmodels.NewsViewModel
import com.newsta.android.utils.models.Category
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_landing.*
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var rawCategories: ArrayList<Category> = ArrayList()
    private var userCategories: ArrayList<Category> = ArrayList()
    private lateinit var userPreferences: UserPreferences
    private var tabPosition = 0
    private lateinit var adapter: ViewPagerAdapter
    private var isAppJustOpened = true

    private fun setUpAdapter() {

        adapter = ViewPagerAdapter(
            childFragmentManager, lifecycle
        )

        println("PAGER ADAPTER ---> $adapter")

        binding.pager.adapter = adapter
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("21466 PAGER: $position")
                categoryState = userCategories[position].categoryId

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

        binding.sideNavDrawer.inflateHeaderView(R.layout.side_nav_header)

        val view = binding.sideNavDrawer.menu.getItem(0).subMenu.getItem(2).actionView
        val modeSwitch = view.findViewById<SwitchCompat>(R.id.switchCompatMode)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            modeSwitch.isChecked = true
            println("Night mode hai")
        } else {
            println("Night mode nahi hai ---> ${AppCompatDelegate.MODE_NIGHT_YES} ---> ${AppCompatDelegate.getDefaultNightMode()}")
            modeSwitch.isChecked = false
        }

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
//                    findNavController().navigate(R.id.action_landingFragment_to_savedStoriesFragment)
                    closeNavigationDrawer()
                    true
                }

                R.id.settings -> {
//                    findNavController().navigate(R.id.action_landingFragment_to_settingsFragment)
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
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    private fun getUserCategories() {
        viewModel.userCategoryLiveData.observe(requireActivity(), Observer {

                val userCats = it
            println("aya hai t $userCats")
            println("aya hai t $rawCategories")
            println("seq usercategoryLiveData")
                createUserCategories(userCats)
                Log.i("Categories", "RAW  $rawCategories")
                Log.i("Categories", "USER  $userCats")
            println(userCategories)


        })

    }

    private fun createUserCategories(userCats: List<Int>?) {
        println("createUserCategories $userCats")
        val tempUserCategories = arrayListOf<Category>()

        val initUserCats = userCategories
        var isSame = true

        userCategories = if(!userCats.isNullOrEmpty()) {
            userCats.forEach { categoryId ->

                rawCategories.forEach { category ->
                    if (categoryId == category.categoryId) {
                        println("${category.categoryId} ---> Present")
                        tempUserCategories.add(Category(category.category, category.categoryId))
                    }
                }
            }
            tempUserCategories
        } else {
            rawCategories
        }
            println("aya hai"+"$isSame ${userCategories}")

            println("seq setCategores in adapter and setUpTablayout")

            adapter.setCategories(userCategories)
            setUpTabLayout()



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            println("RETURNED")
        }

        binding.viewModel = viewModel

        println("Access token: ${NewstaApp.access_token}")

        binding.lifecycleOwner = requireActivity()

        println("VIEWMODEL: $viewModel")

        println("Hey recreated")


        /*binding.notifyCardContainer.setOnClickListener {
//           findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToNotifyFragment())
        }*/

        binding.search.setOnClickListener {
//            val action = LandingFragmentDirections.actionLandingFragmentToSearchFragment()
//            findNavController().navigate(action)
        }

        setUpAdapter()

        viewModel.notifyStoriesLiveData.observe(requireActivity(), Observer {payloads ->
            var count = 0
            if(payloads !=null){
                payloads.forEach {
                    if(it.read == ArticleState.UNREAD){
                        count++
                    }
                }
                if(count <1){
//                    binding.notifyNumberTextContainer.visibility = View.INVISIBLE
                }else{
//                    binding.notifyNumberTextContainer.visibility = View.VISIBLE
//                    binding.notifyNumberText.text = "$count"
                }


            }
        })

        viewModel.categoryLiveData.observe(requireActivity(), Observer {
               //setUpTabLayout(categories)
//                if(categories.isNullOrEmpty()) {
            println("seq categoryLiveData")
            rawCategories = it as ArrayList<Category>
            println("CATEGORIES IN LANDING FRAGMENT ---> $rawCategories")
            getUserCategories()
        })

        viewModel.logoutDataState.observe(requireActivity(), Observer {
            it.getContentIfNotHandled().let {
                when (it) {
                    is DataState.Success -> {
                        Log.i("TAG", "Sucess logout ")

                        viewModel.clearAllData()
                        /*val action =
                            LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
                        findNavController().navigate(action)*/
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

        viewModel.unauthorizedLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {  isUnauthorized ->
                if(isUnauthorized != null) {
                    if (isUnauthorized) {
                        viewModel.clearAllData()
                    /*val action =
                        LandingFragmentDirections.actionLandingFragmentToSignupSigninOptionsFragment()
                    findNavController().navigate(action)*/
                    LoginManager.getInstance().logOut()
                }
                }
            }
        })

        setUpNavigationDrawer()

    }

    private fun setUpTabLayout() {

        TabLayoutMediator(binding.tabLayout, binding.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                /*tab.customView = when (position) {
                    0 -> {
                        if (tabPosition == 0) {
                            addCustomView(
                                userCategories[position].category.capitalize(Locale.ROOT), 16f,
                                Color.WHITE
                            )
                        } else {
                            addCustomView(userCategories[position].category.capitalize(Locale.ROOT))
                        }
                    }
                    else -> {
                        if (position < userCategories.size)
                            addCustomView(userCategories[position].category.capitalize(Locale.ROOT))
                        else
                            return@TabConfigurationStrategy
                    }
                }*/
                tab.text =  userCategories[position].category.capitalize(Locale.ROOT)
            }).attach()

        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {

                tabPosition = tab!!.position
                categoryState = userCategories[tab.position].categoryId

                println("21466 TAB: $tabPosition")


                binding.tabLayout.setScrollPosition(tabPosition, 0f, true)
                binding.pager.currentItem = tabPosition

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

                tabPosition = tab!!.position
                categoryState = userCategories[tab.position].categoryId

                println("21466 TAB: $tabPosition")

                binding.tabLayout.setScrollPosition(tabPosition, 0f, true)
                binding.pager.currentItem = tabPosition

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
        val tab = binding.tabLayout.getTabAt(tabPosition)
        tab?.select()
        super.onResume()
    }

}
