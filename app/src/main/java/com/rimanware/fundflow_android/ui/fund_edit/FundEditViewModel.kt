package com.rimanware.fundflow_android.ui.fund_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund
import fundflow.ledgers.RecurrentTransactionFundView
import java.math.BigDecimal

class FundEditViewModel : ViewModel() {

    private val _title by lazy {
        MutableLiveData<String>().apply { value = "" }
    }
    val title: LiveData<String> by lazy { _title }

    private val _description by lazy {
        MutableLiveData<String>().apply { value = "" }

    }
    val description: LiveData<String> by lazy { _description }

    private val _selectedFund by lazy {
        MutableLiveData<Fund>().apply { value = Fund.empty() }
    }
    private val selectedFund: LiveData<Fund> by lazy { _selectedFund }

    private val _fundFlow by lazy {
        MutableLiveData<BigDecimal>().apply { value = BigDecimal.ZERO }
    }
    val fundFlow: LiveData<BigDecimal> by lazy { _fundFlow }

    private val _inFlow by lazy {
        MutableLiveData<BigDecimal>().apply { value = BigDecimal.ZERO }
    }
    val inFlow: LiveData<BigDecimal> by lazy { _inFlow }

    private val _outFlow by lazy {
        MutableLiveData<BigDecimal>().apply { value = BigDecimal.ZERO }
    }
    val outFlow: LiveData<BigDecimal> by lazy { _outFlow }

    init {
        selectedFund.observeForever(Observer { fund ->
            DataManager.fundView(fund.reference).map { showFund(it) }
        })
    }

    private fun showFund(fundView: RecurrentTransactionFundView): Unit {
        _title.value = fundView.fund.name
        _description.value = fundView.fund.description
        _fundFlow.value = fundView.fundSummaries.summary.fundFlow.flow.value
        _inFlow.value = fundView.fundSummaries.summary.incomingFlow.flow.value
        _outFlow.value = fundView.fundSummaries.summary.outgoingFlow.flow.value
    }

    fun selectFund(fund: Fund): Unit {
        _selectedFund.value = fund
    }
}