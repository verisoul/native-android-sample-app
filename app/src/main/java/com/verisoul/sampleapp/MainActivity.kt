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

        val env : String = "sandbox" // or prod
        val projectId : String = "yourVerisoulProjectId" // or prod projectId

        val mWebView = findViewById<WebView>(R.id.webview)
        mWebView.settings.javaScriptEnabled = true
        mWebView.addJavascriptInterface(JSBridge(this),"JSBridge")
        mWebView.loadUrl("https://webview.$env.verisoul.xyz/?projectId=$projectId")
    }

    class JSBridge(private val context: Context){
        @JavascriptInterface
        fun showMessageInNative(message: String) {
            val apiKey : String = "yourVerisoulApiKey"
            val messageJSON = JSONObject(message)
            Toast.makeText(context, messageJSON.getString("tracking_id"), Toast.LENGTH_LONG).show()

            val json = JSONObject()
            json.put("tracking_id", messageJSON.getString("tracking_id"))
            json.put("account_id", "yourInternalAccountIdentifier")

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://api.sandbox.verisoul.xyz/predict")
                .addHeader("x-api-key", apiKey)
                .post(body)
                .build()

            val client = OkHttpClient()
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                val resStr = response.body?.string()
                print(resStr)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}