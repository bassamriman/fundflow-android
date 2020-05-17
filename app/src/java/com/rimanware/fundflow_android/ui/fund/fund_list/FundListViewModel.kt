package com.rimanware.fundflow_android.ui.fund.fund_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund

const val FUND_LIST_VM_KEY = "fundListViewModelContractKey"

class FundListViewModel : ViewModel(), UpdateFundListViewModelContract, FundListViewModelContract {

    private val _funds by lazy {
        MutableLiveData<List<Fund>>().apply {
            value = DataManager.loadAllFunds()
        }
    }

    override val funds: LiveData<List<Fund>> = _funds

    override fun updateFundList() {
        _funds.apply {
            value = DataManager.loadAllFunds()
        }
    }
}

interface UpdateFundListViewModelContract {
    fun updateFundList(): Unit
}

interface FundListViewModelContract {
    val funds: LiveData<List<Fund>>
}
