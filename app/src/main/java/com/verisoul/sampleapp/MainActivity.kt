package com.verisoul.sampleapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

val env = "sandbox" // or prod
val projectId = "<YOUR PROJECT_ID HERE>" // or prod projectId
val apiKey = "<YOUR API KEY HERE>"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mWebView = findViewById<WebView>(R.id.webview)
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.domStorageEnabled = true // Enable DOM storage API

        mWebView.addJavascriptInterface(JSBridge(this), "JSBridge")
        mWebView.loadUrl("https://js.verisoul.ai/$env/webview.html?project_id=$projectId")
    }

    class JSBridge(private val context: Context) {
        @JavascriptInterface
        fun verisoulHandler(message: String) {
            val messageJSON = JSONObject(message)
            print(messageJSON)
            Toast.makeText(context, messageJSON.getString("session_id"), Toast.LENGTH_LONG).show()

            val json = JSONObject().apply {
                put("session_id", messageJSON.getString("session_id"))
                val accountJson = JSONObject().apply {
                    put("id", "internal-customer-identifier")
                }
                put("account", accountJson)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://api.$env.verisoul.ai/session/authenticate")
                .addHeader("x-api-key", apiKey)
                .post(body)
                .build()

            val client = OkHttpClient()
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                val resStr = response.body?.string()
                if (resStr != null) {
                    Log.d("Response", resStr)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}