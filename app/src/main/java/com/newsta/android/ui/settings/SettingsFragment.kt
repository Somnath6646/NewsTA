package com.newsta.android.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSettingsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.ui.settings.adapters.CustomDropDownAdapter
import com.newsta.android.viewmodels.NewsViewModel

class  SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var fontSizes = arrayListOf<String>()
    lateinit var spinnerAdapter: SpinnerAdapter

    private fun setAdapter(scale: Float) {

        when(scale) {

            NewstaApp.DEFAULT_FONT_SCALE -> fontSizes = arrayListOf(
                NewstaApp.DEFAULT_FONT_NAME,
                NewstaApp.LARGE_FONT_NAME,
                NewstaApp.SMALL_FONT_NAME
            )
            NewstaApp.LARGE_FONT_SCALE -> fontSizes = arrayListOf(
                NewstaApp.LARGE_FONT_NAME,
                NewstaApp.DEFAULT_FONT_NAME,
                NewstaApp.SMALL_FONT_NAME
            )
            NewstaApp.SMALL_FONT_SCALE -> fontSizes = arrayListOf(
                NewstaApp.SMALL_FONT_NAME,
                NewstaApp.LARGE_FONT_NAME,
                NewstaApp.DEFAULT_FONT_NAME
            )
            else -> fontSizes = arrayListOf(
                NewstaApp.DEFAULT_FONT_NAME,
                NewstaApp.LARGE_FONT_NAME,
                NewstaApp.SMALL_FONT_NAME
            )
        }

        val customDropDownAdapter = CustomDropDownAdapter(requireContext(), fontSizes)


        spinnerAdapter = customDropDownAdapter
            .also { adapter ->
                binding.spinnerFontSize.adapter = adapter
            }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.preferences.fontScale.asLiveData().observe(viewLifecycleOwner, Observer {

            val fontScale = it!!
            setAdapter(fontScale)

        })

        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(fontSizes[position]) {

                    NewstaApp.DEFAULT_FONT_NAME -> {
                        viewModel.setFontScale(NewstaApp.DEFAULT_FONT_SCALE)
                        println("DEFAULT SELECTED")
                    }
                    NewstaApp.LARGE_FONT_NAME -> {
                        viewModel.setFontScale(NewstaApp.LARGE_FONT_SCALE)
                        println("LARGE SELECTED")
                    }
                    NewstaApp.SMALL_FONT_NAME -> {
                        viewModel.setFontScale(NewstaApp.SMALL_FONT_SCALE)
                        println("SMALL SELECTED")
                    }



                }

            }

        }

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                Toast.makeText(requireContext(), it!!, Toast.LENGTH_SHORT).show()
            }
        })

        binding.back.setOnClickListener { findNavController().popBackStack() }

        binding.categoriesContainer.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_categoriesFragment) }

    }

    override fun getFragmentView(): Int = R.layout.fragment_settings

}
