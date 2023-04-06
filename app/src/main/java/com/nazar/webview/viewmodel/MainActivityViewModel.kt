package com.nazar.webview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nazar.webview.model.Predict
import com.nazar.webview.repository.MainActivityRepository

class MainActivityViewModel : ViewModel() {

    var predictLiveData: MutableLiveData<Predict>? = null

    fun predict(trackingId: String, accountId: String) : LiveData<Predict>? {
        predictLiveData = MainActivityRepository.getPredictApiCall(trackingId, accountId)
        return predictLiveData
    }

}