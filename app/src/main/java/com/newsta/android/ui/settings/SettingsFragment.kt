package com.newsta.android.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.newsta.android.NewstaApp
import com.newsta.android.R
import com.newsta.android.databinding.FragmentSettingsBinding
import com.newsta.android.ui.base.BaseFragment
import com.newsta.android.viewmodels.NewsViewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel: NewsViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.font_sizes_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                binding.spinnerFontSize.adapter = adapter
            }

        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {

                    0 -> {
                        viewModel.setFontScale(NewstaApp.DEFAULT_FONT_SCALE)
                        println("DEFAULT SELECTED")
                    }
                    1 -> {
                        viewModel.setFontScale(NewstaApp.MEDIUM_FONT_SCALE)
                        println("MEDIUM SELECTED")
                    }
                    2 -> {
                        viewModel.setFontScale(NewstaApp.LARGE_FONT_SCALE)
                        println("LARGE SELECTED")
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

    }

    override fun getFragmentView(): Int = R.layout.fragment_settings

}
