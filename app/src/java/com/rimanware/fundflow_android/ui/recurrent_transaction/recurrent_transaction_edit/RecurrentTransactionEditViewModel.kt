package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.rimanware.fundflow_android.DataManager
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction
import java.math.BigDecimal
import java.time.LocalDateTime

class RecurrentTransactionEditViewModel : ViewModel() {

    private val _spinnerFundContent by lazy {
        MutableLiveData<List<Fund>>().apply { value = DataManager.loadAllFunds() }
    }
    val spinnerFundContent: LiveData<List<Fund>> by lazy { _spinnerFundContent }

    private val _fromFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFund: LiveData<Option<Fund>> by lazy { _fromFund }

    private val _toFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFund: LiveData<Option<Fund>> by lazy { _toFund }

    private val _fromLocalDateTime by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val fromLocalDateTime: LiveData<Option<LocalDateTime>> by lazy { _fromLocalDateTime }

    private val _toLocalDateTime by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val toLocalDateTime: LiveData<Option<LocalDateTime>> by lazy { _toLocalDateTime }

    private val _fundFlowValue by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val fundFlowValue: LiveData<Option<BigDecimal>> by lazy { _fundFlowValue }

    private val _fundFlowUnit by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }
    val fundFlowUnit: LiveData<Option<String>> by lazy { _fundFlowUnit }

    private val _selectedRecurrentTransaction by lazy {
        MutableLiveData<Option<RecurrentTransaction>>().apply { value = Option.empty() }
    }
    private val selectedRecurrentTransaction: LiveData<Option<RecurrentTransaction>> by lazy { _selectedRecurrentTransaction }

    init {
        selectedRecurrentTransaction.observeForever(Observer { maybeRecurrentTransaction ->
            maybeRecurrentTransaction.map { showRecurrentTransaction(it) }
        })
    }

    private fun showRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        _fromFund.value = DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.source)
        _toFund.value = DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.destination)
        _fromLocalDateTime.value = Some(recurrentTransaction.details.recurrence.from)
        _toLocalDateTime.value = Some(recurrentTransaction.details.recurrence.to)
        _fundFlowValue.value = Some(recurrentTransaction.quantification.flow.value)
        _fundFlowUnit.value = Some(recurrentTransaction.quantification.flow.unit.toString())
    }

    fun setFundList(funds: List<Fund>) {
        _spinnerFundContent.value = funds
    }

    fun selectRecurrentTransaction(recurrentTransaction: Option<RecurrentTransaction>) {
        _selectedRecurrentTransaction.value = recurrentTransaction
    }
}
