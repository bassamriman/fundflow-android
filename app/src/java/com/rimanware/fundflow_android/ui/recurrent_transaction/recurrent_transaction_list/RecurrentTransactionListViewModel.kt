package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.ledgers.RecurrentTransaction

const val RECURRENT_TRANSACTION_LIST_VM_KEY = "recurrentTransactionListViewModelContractKey"

class RecurrentTransactionListViewModel : ViewModel(),
    UpdateRecurrentTransactionListViewModelContract {

    private val _recurrentTransactions by lazy {
        MutableLiveData<List<RecurrentTransaction>>().apply {
            value = DataManager.loadAllRecurrentTransactions()
        }
    }

    val recurrentTransactions: LiveData<List<RecurrentTransaction>> = _recurrentTransactions

    override fun updateRecurrentTransactionList() {
        _recurrentTransactions.apply {
            value = DataManager.loadAllRecurrentTransactions()
        }
    }
}

interface UpdateRecurrentTransactionListViewModelContract {
    fun updateRecurrentTransactionList(): Unit
}
