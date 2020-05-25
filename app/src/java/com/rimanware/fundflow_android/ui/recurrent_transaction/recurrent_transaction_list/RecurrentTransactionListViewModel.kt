package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction

const val RECURRENT_TRANSACTION_LIST_VM_KEY = "recurrentTransactionListViewModelContractKey"

class RecurrentTransactionListViewModel : ViewModel(),
    UpdateRecurrentTransactionListViewModelContract {

    // Recurrent Transaction fund dropdown content
    private val _dropdownFundContent by lazy {
        MutableLiveData<List<Fund>>().apply { value = DataManager.loadAllFunds() }
    }
    val dropdownFundContent: LiveData<List<Fund>> by lazy { _dropdownFundContent }

    fun setDropdownFundList(funds: List<Fund>) {
        _dropdownFundContent.value = funds
    }

    // Filters
    private val _fromFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFund: LiveData<Option<Fund>> by lazy { _fromFund }

    private val _toFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFund: LiveData<Option<Fund>> by lazy { _toFund }

    private val _filteredRecurrentTransactions by lazy {
        MutableLiveData<List<RecurrentTransaction>>().apply {
            value = emptyList();
        }
    }

    val filteredRecurrentTransactions: LiveData<List<RecurrentTransaction>> =
        _filteredRecurrentTransactions

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
