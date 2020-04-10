package com.rimanware.fundflow_android.ui.recurrent_transaction_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.ledgers.RecurrentTransaction

class RecurrentTransactionListViewModel : ViewModel() {

    private val _recurrentTransactions by lazy {
        MutableLiveData<List<RecurrentTransaction>>().apply {
            value = DataManager.loadAllRecurrentTransactions()
        }
    }

    val recurrentTransactions: LiveData<List<RecurrentTransaction>> = _recurrentTransactions

    fun updateRecurrentTransactionList(): Unit {
        _recurrentTransactions.apply {
            value = DataManager.loadAllRecurrentTransactions()
        }
    }
}