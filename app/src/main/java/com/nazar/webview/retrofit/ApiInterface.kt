package com.nazar.webview.retrofit

import com.nazar.webview.model.Predict
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers("Content-Type: application/json",
        "x-api-key: 8Ldtkg35pX5YoHu2N3qq89faV6fKvakh1zk7Ps5c"
    )
    @POST("predict")
    fun predict(@Body trackingInfo: HashMap<String, String>) : Call<Predict>

}