package com.rimanware.fundflow_android.ui.fund_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund

class FundListViewModel : ViewModel() {

    private val _funds = MutableLiveData<List<Fund>>().apply {
        value = DataManager.funds()
    }

    val funds: LiveData<List<Fund>> = _funds
}