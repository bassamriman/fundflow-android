package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import com.rimanware.fundflow_android.ui.common.combineTriple
import common.unit.Daily
import common.unit.TimeFrequency
import fundflow.Flow
import fundflow.Fund
import fundflow.ledgers.CombinableRecurrentTransactionFundView
import java.math.BigDecimal
import java.time.LocalDateTime

class FundFlowCardViewViewModel : ViewModel() {

    private val _maybeSelectedDateTime by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val maybeSelectedDateTime: LiveData<Option<LocalDateTime>> by lazy { _maybeSelectedDateTime }

    fun selectDateTime(dateTime: LocalDateTime) {
        _maybeSelectedDateTime.value = Some(dateTime)
    }

    private val _maybeSelectedFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val maybeSelectedFund: LiveData<Option<Fund>> by lazy { _maybeSelectedFund }

    fun selectFund(maybeFund: Option<Fund>) {
        _maybeSelectedFund.value = maybeFund
    }

    private val _inFlow by lazy {
        MutableLiveData<Option<Flow>>().apply { value = Some(Flow(BigDecimal.ZERO, Daily)) }
    }
    val inFlow: LiveData<Option<Flow>> by lazy { _inFlow }

    private val _fundFlow by lazy {
        MutableLiveData<Option<Flow>>().apply { value = Some(Flow(BigDecimal.ZERO, Daily)) }
    }
    val fundFlow: LiveData<Option<Flow>> by lazy { _fundFlow }

    private val _outFlow by lazy {
        MutableLiveData<Option<Flow>>().apply { value = Some(Flow(BigDecimal.ZERO, Daily)) }
    }
    val outFlow: LiveData<Option<Flow>> by lazy { _outFlow }

    private val _dateTimeToComputeFlowAt by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val dateTimeToComputeFlowAt: LiveData<Option<LocalDateTime>> by lazy { _dateTimeToComputeFlowAt }

    private val _selectedFundFlowView by lazy {
        MutableLiveData<Option<CombinableRecurrentTransactionFundView>>().apply { value = None }
    }
    val selectedFundFlowView: LiveData<Option<CombinableRecurrentTransactionFundView>> by lazy { _selectedFundFlowView }

    private val _fundFlowTimeFrequency by lazy {
        MutableLiveData<Option<TimeFrequency>>().apply { value = Some(Daily) }
    }
    val fundFlowTimeFrequency: LiveData<Option<TimeFrequency>> by lazy { _fundFlowTimeFrequency }

    val computationDateTimeAndFundTriple: LiveData<Triple<Option<Fund>, Option<LocalDateTime>, Option<TimeFrequency>>> =
        combineTriple(maybeSelectedFund, dateTimeToComputeFlowAt, fundFlowTimeFrequency)

    fun setFundFlowTimeFrequency(maybeTimeFrequency: Option<TimeFrequency>) {
        _fundFlowTimeFrequency.value = maybeTimeFrequency
    }

    fun setDateTimeToComputeFlowAt(computeAt: Option<LocalDateTime>) {
        _dateTimeToComputeFlowAt.value = computeAt
    }

    fun selectFundFlowView(selected: Option<CombinableRecurrentTransactionFundView>) {
        _selectedFundFlowView.value = selected
    }

    fun showFund(maybeFundFlowView: Option<CombinableRecurrentTransactionFundView>) {
        maybeFundFlowView.map {
            _inFlow.value = Some(it.fundSummaries.summary.incomingFlow.flow)
            _fundFlow.value = Some(it.fundSummaries.summary.fundFlow.flow)
            _outFlow.value = Some(it.fundSummaries.summary.outgoingFlow.flow)
        }.getOrElse {
            _inFlow.value = Some(Flow(BigDecimal.ZERO, Daily))
            _fundFlow.value = Some(Flow(BigDecimal.ZERO, Daily))
            _outFlow.value = Some(Flow(BigDecimal.ZERO, Daily))
        }
    }
}
