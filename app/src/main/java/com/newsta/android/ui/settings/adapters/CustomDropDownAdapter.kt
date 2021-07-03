package com.newsta.android.ui.settings.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.newsta.android.R
import com.newsta.android.databinding.CustomSpinnerItemBinding

class CustomDropDownAdapter(val context: Context, var dataSource: List<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            val binding = DataBindingUtil.inflate<CustomSpinnerItemBinding>(inflater,R.layout.custom_spinner_item, parent, false )
            view = binding.root
            vh = ItemHolder(binding)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = dataSource.get(position)

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(binding: CustomSpinnerItemBinding) {
        val label: TextView

        init {
            label = binding.textSpinner
        }
    }

}