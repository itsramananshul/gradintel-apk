package com.gradintel.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gradintel.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge + dark status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.parseColor("#07070e")
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView     = binding.webView
        swipeRefresh = binding.swipeRefresh

        setupSwipeRefresh()
        setupWebView()
        setupBackNavigation()

        if (isNetworkAvailable()) {
            loadApp()
        } else {
            showOfflineState()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(
            Color.parseColor("#818cf8"),
            Color.parseColor("#f472b6"),
            Color.parseColor("#34d399")
        )
        swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#0f0f1a"))
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.mediaPlaybackRequiresUserGesture = false
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportMultipleWindows(false)

        // Enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.setBackgroundColor(Color.parseColor("#07070e"))

        // WebChromeClient for JS dialogs, file pickers, console logs
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
                return true
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    swipeRefresh.isRefreshing = false
                    binding.loadingOverlay.animate()
                        .alpha(0f).setDuration(400)
                        .withEndAction { binding.loadingOverlay.visibility = View.GONE }
                }
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                result?.confirm()
                return true
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeRefresh.isRefreshing = false
                // Inject viewport fix and hide custom cursor (not needed on mobile)
                view?.evaluateJavascript("""
                    (function() {
                        // Hide desktop cursor
                        var style = document.createElement('style');
                        style.textContent = '#cur, #cur-ring { display: none !important; } body { cursor: auto !important; }';
                        document.head.appendChild(style);
                        
                        // Add safe area padding for notch devices
                        var metaStyle = document.createElement('style');
                        metaStyle.textContent = '.app { padding-bottom: max(100px, env(safe-area-inset-bottom) + 80px) !important; } .nav-tabs { padding-bottom: env(safe-area-inset-bottom) !important; }';
                        document.head.appendChild(metaStyle);
                    })();
                """.trimIndent(), null)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    showOfflineState()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                // Keep local file navigation within WebView
                if (url.startsWith("file://") || url.startsWith("http://") || url.startsWith("https://")) {
                    return false
                }
                return true
            }
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun loadApp() {
        binding.offlineState.visibility = View.GONE
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.loadingOverlay.alpha = 1f
        webView.loadUrl("file:///android_asset/www/index.html")
    }

    private fun showOfflineState() {
        swipeRefresh.isRefreshing = false
        binding.loadingOverlay.visibility = View.GONE
        binding.offlineState.visibility = View.VISIBLE
        binding.retryBtn.setOnClickListener {
            if (isNetworkAvailable()) {
                binding.offlineState.visibility = View.GONE
                loadApp()
            } else {
                Toast.makeText(this, "Still offline. Check your connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
