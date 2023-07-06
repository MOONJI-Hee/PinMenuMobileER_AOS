package com.wooriyo.pinmenumobileer.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivityQrBinding
import com.wooriyo.pinmenumobileer.order.OrderListActivity
import java.net.URISyntaxException

class QrActivity : AppCompatActivity() {
    lateinit var binding: ActivityQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ordcode = intent.getStringExtra("ordcode")

        val uriData = intent.data
        val status = uriData?.getQueryParameter("status")?.toInt()

        if(status != null) {
            when(status) {
                1 -> finish()
                2 -> Toast.makeText(this@QrActivity, R.string.msg_payment_cancel, Toast.LENGTH_SHORT).show()
            }
        }

        binding.webview.run {
            webViewClient = customWebClient(this@QrActivity)
            webChromeClient = WebChromeClient()
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.supportZoom()
            settings.javaScriptEnabled = true
            settings.supportMultipleWindows()
            settings.javaScriptCanOpenWindowsAutomatically = true
        }

        binding.webview.loadUrl(AppProperties.SERVER + "m/pg.php?useridx=${MyApplication.useridx}&ordcode=$ordcode")

        binding.back.setOnClickListener { finish() }

    }

    class customWebClient(val context: Context) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val intent = parse(url)
            if (intent != null) {
                val uriData = intent.data
                val status = uriData?.getQueryParameter("status")?.toInt()

                if (status != null) {
                    when (status) {
                        1 -> {
                            context.startActivity(Intent(context, OrderListActivity::class.java))
                        }
                        2 -> {
                            Toast.makeText(context, R.string.msg_payment_cancel, Toast.LENGTH_SHORT).show()
                            context as Activity
                            context.finish()
                        }
                    }
                }
            }
            return true
        }
        private fun parse(url: String): Intent? {
            return try {
                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } catch (e: URISyntaxException) {
                null
            }
        }
    }
}