package com.newsta.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.newsta.android.R
import com.newsta.android.databinding.ContainerSlidingItemBinding
import com.newsta.android.models.SliderItem

class OnboardingSliderAdapter(private val sliderItems: ArrayList<SliderItem>,

                              private val context: Context  ) : PagerAdapter() {

    private lateinit var layoutInflater: LayoutInflater


    override fun isViewFromObject(view: View, `object`: Any): Boolean = view.equals(`object`)

    override fun getCount(): Int = sliderItems.size


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = LayoutInflater.from(context)

        val binding = DataBindingUtil.inflate<ContainerSlidingItemBinding>(layoutInflater, R.layout.container_sliding_item, container, false)

        binding.imgSlidingItem.setImageResource(sliderItems[position].image)
        binding.descrSlidingItem.setText(sliderItems[position].string)

        container.addView(binding.root, 0)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}