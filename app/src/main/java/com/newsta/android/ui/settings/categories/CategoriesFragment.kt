package com.newsta.android.ui.settings.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.newsta.android.viewmodels.NewsViewModel

class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>() {

    private val viewModel by activityViewModels<NewsViewModel>()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var categories: ArrayList<Category>
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(UP or
                    DOWN or
                    START or
                    END, 0) {

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.categoryDataState.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                    categories = it.data as ArrayList<Category>
                    setUpCategoriesAdapter()

                }
                is DataState.Error -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState Error")
                    Toast.makeText(requireContext(), it.exception, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                }
            }
        })

        binding.back.setOnClickListener { findNavController().popBackStack() }

    }

    private fun setUpCategoriesAdapter() {

        categoriesAdapter = CategoriesAdapter { viewHolder -> startDragging(viewHolder) }
        binding.recyclerViewCategories.adapter = categoriesAdapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        categoriesAdapter.addAll(categories)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCategories)

    }

    private fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun getFragmentView(): Int = R.layout.fragment_categories

}
