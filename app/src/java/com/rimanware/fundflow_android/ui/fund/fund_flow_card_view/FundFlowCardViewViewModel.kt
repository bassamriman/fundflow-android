package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.*
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.flatten
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.ui.common.combineTuple
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

    private val selectedDateTimeAndFundTuple: LiveData<Pair<Option<Fund>, Option<LocalDateTime>>> =
        combineTuple(maybeSelectedFund, maybeSelectedDateTime)

    private val _selectedFundFlowView by lazy {
        MutableLiveData<Option<CombinableRecurrentTransactionFundView>>().apply { value = None }
    }
    private val selectedFundFlowView: LiveData<Option<CombinableRecurrentTransactionFundView>> by lazy { _selectedFundFlowView }

    init {
        selectedDateTimeAndFundTuple.observeForever { maybeSelectedDateTimeAndFundTuple ->
            val maybeFundFlowView = maybeSelectedDateTimeAndFundTuple.toOption().map {
                val (maybeFund, maybeLocalDateTime) = it
                Option.applicative().map(
                    maybeFund,
                    maybeLocalDateTime
                ) { (previouslySelectedFund, selectedDateTime) ->
                    val a = DataManager.loadFundFlowView(
                        previouslySelectedFund.reference,
                        selectedDateTime
                    )
                    a
                }.fix().flatten()
            }.flatten()
            _selectedFundFlowView.value = maybeFundFlowView
        }

        selectedFundFlowView.observeForever { showFund(it) }
    }

    private fun showFund(maybeFundFlowView: Option<CombinableRecurrentTransactionFundView>): Unit {
        maybeFundFlowView.map {
            _inFlow.value = Some(it.fundSummaries.summary.incomingFlow.flow.value)
            _fundFlow.value = Some(it.fundSummaries.summary.fundFlow.flow.value)
            _outFlow.value = Some(it.fundSummaries.summary.outgoingFlow.flow.value)
        }.getOrElse {
            _inFlow.value = Some(BigDecimal.ZERO)
            _fundFlow.value = Some(BigDecimal.ZERO)
            _outFlow.value = Some(BigDecimal.ZERO)
        }
    }
}