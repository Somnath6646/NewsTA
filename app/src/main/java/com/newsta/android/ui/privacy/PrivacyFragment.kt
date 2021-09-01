package com.newsta.android.ui.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.newsta.android.R
import com.newsta.android.databinding.FragmentPrivacyBinding
import com.newsta.android.ui.base.BaseFragment


class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Inflate the layout for this fragment

        binding.webview.webViewClient = WebViewClient()

        // this will load the url of the website
        val url = "http://www.newsta.in/privacy"
        binding.webview.loadUrl(url)

        // this will enable the javascript settings
        binding.webview.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        binding.webview.settings.setSupportZoom(true)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun getFragmentView(): Int =  R.layout.fragment_privacy

}