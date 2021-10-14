package com.newsta.android.ui.authentication

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.newsta.android.R
import com.newsta.android.databinding.FragmentTutorialBinding
import com.newsta.android.ui.authentication.adapter.TutorialAdapter
import com.newsta.android.ui.base.BaseFragment

class TutorialFragment : BaseFragment<FragmentTutorialBinding>() {

    private lateinit var adapter: TutorialAdapter
    var count = 0

    private fun navigate() {
        findNavController().navigate(R.id.action_tutorialFragment_to_signupSigninOptionsFragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = TutorialAdapter()
        binding.pager.adapter = adapter
        binding.pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.setViewPager2(binding.pager)

        binding.nextBtn.setOnClickListener {
            if (count == adapter.listSize - 1) {
                println("COUNT ---> $count SIZE ---> ${adapter.listSize}")
                navigate()
            } else {
                count++
                binding.pager.setCurrentItem(count, true)
            }
        }

        binding.skipBtn.setOnClickListener {
            count = adapter.listSize - 1;
            navigate()
        }

        binding.pager.setPageTransformer { page, position ->
            println("POSITION ---> ${binding.pager.currentItem}")
            count = binding.pager.currentItem
        }

    }

    override fun getFragmentView(): Int = R.layout.fragment_tutorial

}
