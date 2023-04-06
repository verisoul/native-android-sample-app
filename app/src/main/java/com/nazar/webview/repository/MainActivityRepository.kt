package com.nazar.webview.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nazar.webview.model.Predict
import com.nazar.webview.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MainActivityRepository {

    val predictInfo = MutableLiveData<Predict>()

    fun getPredictApiCall(trackingId: String, accountId: String): MutableLiveData<Predict> {
        val trackingInfo = HashMap<String, String>();
        trackingInfo["trackingId"] = trackingId
        trackingInfo["accountId"] = accountId

        val call = RetrofitClient.apiInterface.predict(trackingInfo)

        call.enqueue(object: Callback<Predict> {
            override fun onFailure(call: Call<Predict>, t: Throwable) {
                // TODO("Not yet implemented")
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<Predict>,
                response: Response<Predict>
            ) {
                // TODO("Not yet implemented")
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()

                if(data != null){
                    val msg = data.accountId
                    predictInfo.value = Predict(msg)
                }
            }
        })

        return predictInfo
    }
}