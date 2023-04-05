package com.nazar.webview.retrofit

import com.nazar.webview.model.ServicesSetterGetter
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @POST("test")
    fun getServices() : Call<ServicesSetterGetter>

}