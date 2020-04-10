package com.rimanware.fundflow_android.ui.fund_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund

class FundListViewModel : ViewModel() {

    private val _funds by lazy {
        MutableLiveData<List<Fund>>().apply {
            value = DataManager.loadAllFunds()
        }
    }

    val funds: LiveData<List<Fund>> = _funds

    fun updateFundList(): Unit {
        _funds.apply {
            value = DataManager.loadAllFunds()
        }
    }
}