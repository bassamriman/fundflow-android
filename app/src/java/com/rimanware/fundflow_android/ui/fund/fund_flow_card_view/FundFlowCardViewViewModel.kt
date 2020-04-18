package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund
import fundflow.ledgers.RecurrentTransactionFundView
import java.math.BigDecimal

class FundFlowCardViewViewModel : ViewModel() {

    private val _selectedFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val selectedFund: LiveData<Option<Fund>> by lazy { _selectedFund }

    fun selectFund(maybeFund: Option<Fund>) {
        _selectedFund.value = maybeFund
    }

    private val _inFlow by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val inFlow: LiveData<Option<BigDecimal>> by lazy { _inFlow }

    private val _fundFlow by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val fundFlow: LiveData<Option<BigDecimal>> by lazy { _fundFlow }

    private val _outFlow by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val outFlow: LiveData<Option<BigDecimal>> by lazy { _outFlow }

    init {
        selectedFund.observeForever { maybeFund: Option<Fund> ->
            maybeFund.map { fund ->
                DataManager.loadFundView(fund.reference).map { showFund(it) }
            }
        }
    }

    private fun showFund(fundView: RecurrentTransactionFundView): Unit {
        _inFlow.value = Some(fundView.fundSummaries.summary.incomingFlow.flow.value)
        _fundFlow.value = Some(fundView.fundSummaries.summary.fundFlow.flow.value)
        _outFlow.value = Some(fundView.fundSummaries.summary.outgoingFlow.flow.value)
    }
}