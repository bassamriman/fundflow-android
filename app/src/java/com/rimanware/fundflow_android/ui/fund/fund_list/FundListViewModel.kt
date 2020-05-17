package com.rimanware.fundflow_android.ui.fund.fund_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund

class FundListViewModel : ViewModel(), UpdateFundListViewModelContract {

    private val _funds by lazy {
        MutableLiveData<List<Fund>>().apply {
            value = DataManager.loadAllFunds()
        }
    }

    val funds: LiveData<List<Fund>> = _funds

    override fun updateFundList() {
        _funds.apply {
            value = DataManager.loadAllFunds()
        }
    }
}

interface UpdateFundListViewModelContract {
    fun updateFundList(): Unit
}
