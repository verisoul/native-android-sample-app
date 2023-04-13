package com.verisoul.sampleapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mWebView = findViewById<WebView>(R.id.webview)
        mWebView.settings.javaScriptEnabled = true
        mWebView.addJavascriptInterface(JSBridge(this),"JSBridge")
        mWebView.loadUrl("https://webview.dev.verisoul.xyz/?projectId=1234")
    }

    class JSBridge(private val context: Context){
        @JavascriptInterface
        fun showMessageInNative(message: String) {
            val messageJSON = JSONObject(message)
            Toast.makeText(context, messageJSON.getString("tracking_id"), Toast.LENGTH_LONG).show()

            val json = JSONObject()
            json.put("trackingId", messageJSON.getString("tracking_id"))
            json.put("accountId", "randomString")

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://api.prod.verisoul.xyz/predict")
                .addHeader("x-api-key", "8Ldtkg35pX5YoHu2N3qq89faV6fKvakh1zk7Ps5c")
                .post(body)
                .build()

            val client = OkHttpClient()
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                val resStr = response.body?.string()
                resStr?.let {
                    val accountId = JSONObject(it).getString("accountId")
                    print(accountId)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}