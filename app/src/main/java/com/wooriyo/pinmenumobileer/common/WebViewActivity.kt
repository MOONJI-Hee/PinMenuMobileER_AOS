package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity() {
    lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url").toString()
        val strTitle = intent.getStringExtra("title").toString()
        binding.title.text = strTitle

        binding.webview.run {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            loadUrl(url)
        }

        binding.back.setOnClickListener { finish() }
    }
}