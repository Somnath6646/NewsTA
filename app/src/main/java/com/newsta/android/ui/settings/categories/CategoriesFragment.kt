package com.newsta.android.ui.settings.categories

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newsta.android.R
import com.newsta.android.databinding.FragmentCategoriesBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.settings.categories.adapter.CategoriesAdapter
import com.newsta.android.utils.models.Category
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.UserCategory
import com.newsta.android.viewmodels.NewsViewModel

class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>() {

    private val viewModel by activityViewModels<NewsViewModel>()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var categories: ArrayList<Category>
    private var userCategories: ArrayList<UserCategory> = arrayListOf()
    private lateinit var userCategoryIds: List<Int>
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            UP or
                    DOWN, 0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val adapter = recyclerView.adapter as CategoriesAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                adapter.notifyItemMoved(from, to)

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f
            }
        }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun getUserCategories() {

        /*viewModel.getUserPreferences()
        viewModel.userPreferencesDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: UserCategoryDatState Success OBSERVE ---> ${it.data}")
                    Log.i("TAG", "CATEGORIES ---> $categories")
                    userCategoryIds = it.data
                    setUserCategories()
                    Log.i("TAG", "USER CATEGORIES ---> $userCategories")
                    setUpCategoriesAdapter()
                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Error")
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState loading")
                }
            }
        })*/

        viewModel.userCategoryLiveData.observe(viewLifecycleOwner, Observer {

                if (it != null) {
                    userCategoryIds = it
                }
                setUserCategories()
                setUpCategoriesAdapter()
            })


    }

    private fun setUserCategories() {
        userCategories.clear()
        if(userCategoryIds.size ?: 0 == 0) {
            categories.forEach { category ->
                println("${category.categoryId} ---> Present")
                userCategories.add(UserCategory(category = category.category, categoryId = category.categoryId, isEnabled = true))
            }
        } else {
            userCategoryIds.forEach { categoryId ->
                categories.forEach { category ->
                    if (categoryId == category.categoryId) {
                        println("${category.categoryId} ---> Present")
                        userCategories.add(
                            UserCategory(
                                category = category.category,
                                categoryId = category.categoryId,
                                isEnabled = true
                            )
                        )
                    }
                }
            }
            categories.forEach { category ->
                if (!userCategoryIds.contains(category.categoryId)) {
                    println("${category.categoryId} ---> Present")
                    userCategories.add(
                        UserCategory(
                            category = category.category,
                            categoryId = category.categoryId,
                            isEnabled = false
                        )
                    )
                }
            }
        }
    }

    private fun saveUserCategories() {

        viewModel.saveUserCategories(userCategoryIds as ArrayList<Int>)
        viewModel.userCategoriesSaveDataState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    Log.i("TAG", "onActivityCreated: UserCategoryDatState Success ON SAVE ---> ${it.data}")
                    val userPref = it.data
                    viewModel.setUserCategoryLiveData(userPref as ArrayList<Int>)
                    println("CATEGORIES ---> $categories")
                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: UserCategoryDatState Error ON SAVE ---> ${it.exception}")
                    if (it.statusCode == 101)
                        viewModel.toast("Cannot save user preferences")
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: UserCategoryDatState ON SAVE loading")
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.categoryLiveData.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()) {
                categories = it as ArrayList<Category>
                println("CATEGORIES MILL GAYI ---> $categories")
                getUserCategories()
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (!it.isNullOrEmpty())
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.back.setOnClickListener { findNavController().popBackStack() }

        binding.save.setOnClickListener {
            println("SAVING PREF")
            saveUserCategories()
        }

    }

    private fun setUpCategoriesAdapter() {

        println("Adapter\nCATEGORIES ---> $categories\nUSER CATEGORIES ---$userCategories")

        categoriesAdapter = CategoriesAdapter({ viewHolder -> startDragging(viewHolder) },
            { category, isRemoved -> onCategoryChange(category, isRemoved) },
            { from, to -> onCategoryPositionChange(from, to) })
        binding.recyclerViewCategories.adapter = categoriesAdapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        categoriesAdapter.addAll(categories, userCategories)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCategories)

    }

    private fun onCategoryChange(category: UserCategory, isChecked: Boolean) {
        println("CATEGORY ---> $category ---- isRemoved ---> $isChecked")
        userCategories[userCategories.indexOf(category)].isEnabled = isChecked
        userCategoriesToUserPreferences()
        println("USER CATEGORY AFTER REMOVAL ---> $userCategories")
        println("USER CATEGORY PREF AFTER REMOVAL ---> ${userCategoryIds}")
    }

    private fun onCategoryPositionChange(from: Int, to:Int) {
        val oldCategory = userCategories[from]
        userCategories.removeAt(from)
        userCategories.add(to, oldCategory)
        userCategoriesToUserPreferences()
        println("USER CATEGORY AFTER REORDER ---> $userCategories")
        println("USER CATEGORY PREF AFTER REMOVAL ---> ${userCategoryIds}")
    }

    private fun userCategoriesToUserPreferences() {
        val userCats = arrayListOf<Int>()
        userCategories.forEach { category ->
            if(category.isEnabled) {
                userCats.add(category.categoryId)
            }
        }
        userCategoryIds = userCats
    }

    private fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun getFragmentView(): Int = R.layout.fragment_categories

}
