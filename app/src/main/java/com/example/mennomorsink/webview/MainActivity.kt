package com.example.mennomorsink.webview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.example.mennomorsink.webview.config.Config

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var pinningToggle: SwitchCompat
    private lateinit var whitelistToggle: SwitchCompat
    private lateinit var btnA: Button
    private lateinit var btnB: Button
    private lateinit var logTextView: TextView

    private val url1 = "https://infosupport.com"
    private val url2 = "https://security.stackexchange.com"

    private var pinningEnabled = true
    private var whitelistingEnabled = true

    private val onLoadedListener = object : SecureWebViewClient.OnResultListener {
        override fun onLoaded(url: String) {
            runOnUiThread { logTextView.text = getString(R.string.loaded, url) }
        }

        override fun onPreventedLoading(reason: String) {
            runOnUiThread { logTextView.text = getString(R.string.loading_prevented, reason) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById<View>(R.id.webView) as WebView
        pinningToggle = findViewById(R.id.pinning_switch)
        whitelistToggle = findViewById(R.id.whitelisting_switch)
        btnA = findViewById<View>(R.id.btn1) as Button
        btnB = findViewById<View>(R.id.btn2) as Button
        logTextView = findViewById<View>(R.id.textView) as TextView

        setupWebView(SecureWebViewClient(Config.createSecureOkHttpClient()))
        wireButtons()
        wireToggles()
    }

    private fun setupWebView(webViewClient: SecureWebViewClient) {
        webViewClient.setOnLoaderListener(onLoadedListener)
        webView.webViewClient = webViewClient
    }

    private fun wireButtons() {
        btnA.setOnClickListener {
            webView.loadUrl("about:blank")
            logTextView.text = ""
            webView.loadUrl(url1)
        }

        btnB.setOnClickListener {
            webView.loadUrl("about:blank")
            logTextView.text = ""
            webView.loadUrl(url2)
        }
    }

    private fun wireToggles() {
        pinningToggle.setOnCheckedChangeListener { _, isChecked ->
            pinningEnabled = isChecked
            onSwitchToggled()
        }
        whitelistToggle.setOnCheckedChangeListener { _, isChecked ->
            whitelistingEnabled = isChecked
            onSwitchToggled()
        }

    }

    private fun onSwitchToggled() {
        if(pinningEnabled && whitelistingEnabled) {
            setupWebView(SecureWebViewClient(Config.createSecureOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup for pinning and whitelisting")
        } else if (pinningEnabled) {
            setupWebView(SecureWebViewClient(Config.createPinningOnlyOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup for pinning only")
        } else if (whitelistingEnabled) {
            setupWebView(SecureWebViewClient(Config.createWhitelistingOnlyOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup for whitelisting only")
        } else {
            setupWebView(SecureWebViewClient(Config.createInsecureOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup with no pinning and whitelisting")
        }
    }
}
