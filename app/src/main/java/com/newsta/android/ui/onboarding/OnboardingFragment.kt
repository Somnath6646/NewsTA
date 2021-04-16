package com.newsta.android.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.newsta.android.R
import com.newsta.android.adapter.OnboardingSliderAdapter
import com.newsta.android.databinding.FragmentOnboardingBinding
import com.newsta.android.models.SliderItem
import com.newsta.android.ui.onboarding.OnboardingFragmentDirections

class OnboardingFragment : Fragment() {

    private lateinit var adapter: OnboardingSliderAdapter
    private lateinit var sliderItems: ArrayList<SliderItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentOnboardingBinding>(inflater,
            R.layout.fragment_onboarding, container, false )

        sliderItems = ArrayList()
        sliderItems.add(SliderItem(R.drawable.ic_1img_sliding_item, "To enjoy latest news around the world in the briefest way possible log in or create your  free account now"))
        sliderItems.add(SliderItem(R.drawable.ic_1img_sliding_item, "To enjoy latest news form around the world in the briefest way possible log in or create your  free account now"))
        sliderItems.add(SliderItem(R.drawable.ic_1img_sliding_item, "To enjoy news form around the world in the briefest way possible log in or create your  free account now"))

        adapter = OnboardingSliderAdapter(sliderItems, requireContext())
        binding.slider.adapter = adapter

        val dotscount = adapter.count

        val dots = arrayOfNulls<ImageView>(dotscount)

        for (i in 0 until dotscount) {
            dots[i] = ImageView(requireActivity())
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(requireActivity(),
                    R.drawable.inactive_dot
                ))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.sliderDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(), R.drawable.active_dot
            )
        )


        binding.slider.setOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int){}

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotscount) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.inactive_dot
                        )
                    )
                }
                dots[position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.active_dot
                    )
                )
            }
        })

        binding.btnNext.setOnClickListener {
            Log.i("MYTAG", "${binding.slider.currentItem}")
            if(binding.slider.currentItem < adapter.count-1){
                binding.slider.setCurrentItem(binding.slider.currentItem+1, true)

            }
            else{
                val action =
                    OnboardingFragmentDirections.actionOnboardingFragmentToSignupSigninOptionsFragment()
                findNavController().navigate(action)    }

        }

        binding.btnSkip.setOnClickListener {
            val action =
                OnboardingFragmentDirections.actionOnboardingFragmentToSignupSigninOptionsFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }


}