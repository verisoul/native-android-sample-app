package com.nazar.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nazar.webview.databinding.ActivityMainBinding
import com.nazar.webview.viewmodel.MainActivityViewModel
import org.json.JSONObject
import kotlin.streams.asSequence

class MainActivity : AppCompatActivity() {
    public val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel

    private val MAX_PROGRESS = 100

    private lateinit var pageUrl: String

    interface TrackingIdInterface {
        fun complete(trackingId: String?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        Log.e(TAG, "Hello");
//        pageUrl = "https://c.prod.verisoul.xyz/?projectId=test1234";
        pageUrl = "https://c.nonprod.verisoul.xyz/?projectId=test1234"
        init()
        initWebView()
        setWebClient()
        loadUrl(pageUrl)
    }

    private fun init(){
        binding.swipe.setOnRefreshListener {
            binding.webView.reload();
            binding.swipe.isRefreshing = false;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.addJavascriptInterface(JSBridge(this, object: TrackingIdInterface{
            override fun complete(trackingId: String?) {
                trackingId?.let { callPredict(it) }
            }
        }), "JSBridge");
        binding.webView.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.e(TAG + "PageStarted", url!!)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e(TAG, url!!);
                if (Uri.parse(url).host == "www.example.com") {
                    // This is my web site, so do not override; let my WebView load the page
                    return false
                }

                return true
            }
        }
    }

    private fun setWebClient() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
                if (newProgress < MAX_PROGRESS && binding.progressBar.visibility == ProgressBar.GONE) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }
                if (newProgress == MAX_PROGRESS) {
                    binding.progressBar.visibility = ProgressBar.GONE
                }
            }
        }
    }

    private fun loadUrl(pageUrl: String) {
        binding.webView.loadUrl(pageUrl)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    class JSBridge(private val context: Context, private val trackingIdListener: TrackingIdInterface){
        @JavascriptInterface
        fun showMessageInNative(message:String){
            val response = JSONObject(message)
            if(response.get("status") == "success"){
                trackingIdListener.complete(response.get("trackingId").toString())
            }else{
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
            Log.e("MainActivity", message)
        }
    }

    private fun callPredict(trackingId: String){
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var accountId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            accountId = java.util.Random().ints(6, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
        }
        Log.e(TAG, accountId)
        viewModel.predict(trackingId, accountId)
    }
}