package com.example.learningportalapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var etUrl: EditText
    private lateinit var progressBar: ProgressBar

    private val homeUrl = "https://www.google.com" // you can change to university portal

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        etUrl = findViewById(R.id.etUrl)
        progressBar = findViewById(R.id.progressBar)

        // Toolbar buttons
        findViewById<Button>(R.id.btnBack).setOnClickListener { goBack() }
        findViewById<Button>(R.id.btnForward).setOnClickListener { goForward() }
        findViewById<Button>(R.id.btnRefresh).setOnClickListener { webView.reload() }
        findViewById<Button>(R.id.btnHome).setOnClickListener { loadUrlSmart(homeUrl) }

        // Go button and keyboard Done
        findViewById<Button>(R.id.btnGo).setOnClickListener {
            loadUrlSmart(etUrl.text.toString())
        }

        etUrl.setOnEditorActionListener { _, actionId, event ->
            val isDone = actionId == EditorInfo.IME_ACTION_DONE
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (isDone || isEnter) {
                loadUrlSmart(etUrl.text.toString())
                true
            } else false
        }

        // Shortcut buttons
        findViewById<Button>(R.id.btnGoogle).setOnClickListener { loadUrlSmart("https://www.google.com") }
        findViewById<Button>(R.id.btnYouTube).setOnClickListener { loadUrlSmart("https://www.youtube.com") }
        findViewById<Button>(R.id.btnWikipedia).setOnClickListener { loadUrlSmart("https://www.wikipedia.org") }
        findViewById<Button>(R.id.btnKhan).setOnClickListener { loadUrlSmart("https://www.khanacademy.org") }
        findViewById<Button>(R.id.btnUniversity).setOnClickListener {
            // Replace this with your university portal URL if you have one
            loadUrlSmart("https://www.edx.org") // safe educational placeholder
        }

        // WebView settings
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        // Keep navigation inside WebView (no external browser)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false // load inside webview
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
                if (url != null) etUrl.setText(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                if (url != null) etUrl.setText(url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // Only show offline page for main frame
                if (request?.isForMainFrame == true) {
                    showOfflinePage()
                }
            }
        }

        // Track loading progress
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = newProgress
                if (newProgress >= 100) progressBar.visibility = View.GONE
            }
        }

        // Load home
        loadUrlSmart(homeUrl)
    }

    private fun loadUrlSmart(input: String) {
        val url = normalizeUrl(input)

        if (!isOnline()) {
            showOfflinePage()
            return
        }

        webView.loadUrl(url)
        etUrl.setText(url)
    }

    private fun normalizeUrl(input: String): String {
        var u = input.trim()
        if (u.isEmpty()) return homeUrl

        // If user types "google.com", add https://
        if (!u.startsWith("http://") && !u.startsWith("https://")) {
            u = "https://$u"
        }
        return u
    }

    private fun showOfflinePage() {
        Toast.makeText(this, "Offline mode: showing local page", Toast.LENGTH_SHORT).show()
        webView.loadUrl("file:///android_asset/offline.html")
        etUrl.setText("offline.html")
        progressBar.visibility = View.GONE
    }

    private fun goBack() {
        if (webView.canGoBack()) webView.goBack()
        else Toast.makeText(this, "No more history", Toast.LENGTH_SHORT).show()
    }

    private fun goForward() {
        if (webView.canGoForward()) webView.goForward()
        else Toast.makeText(this, "No forward history", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Handle Android back button: navigate WebView history
    @SuppressLint("GestureBackNavigation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}