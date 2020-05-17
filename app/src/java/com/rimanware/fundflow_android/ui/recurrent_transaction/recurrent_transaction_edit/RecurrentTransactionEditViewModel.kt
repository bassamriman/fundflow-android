package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.rimanware.fundflow_android.DataManager
import common.unit.TimeFrequency
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction
import java.math.BigDecimal
import java.time.LocalDateTime

class RecurrentTransactionEditViewModel : ViewModel() {

    private val _dropdownFundContent by lazy {
        MutableLiveData<List<Fund>>().apply { value = DataManager.loadAllFunds() }
    }
    val dropdownFundContent: LiveData<List<Fund>> by lazy { _dropdownFundContent }

    private val _fromFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFund: LiveData<Option<Fund>> by lazy { _fromFund }

    private val _fromFundToSave by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFundToSave: LiveData<Option<Fund>> by lazy { _fromFundToSave }

    private val _toFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFund: LiveData<Option<Fund>> by lazy { _toFund }

    private val _toFundToSave by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFundToSave: LiveData<Option<Fund>> by lazy { _toFundToSave }

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

    private val _fundFlowValueToSave by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val fundFlowValueToSave: LiveData<Option<BigDecimal>> by lazy { _fundFlowValueToSave }

    private val _fundFlowTimeFrequency by lazy {
        MutableLiveData<Option<TimeFrequency>>().apply { value = None }
    }
    val fundFlowTimeFrequency: LiveData<Option<TimeFrequency>> by lazy { _fundFlowTimeFrequency }

    private val _fundFlowTimeFrequencyToSave by lazy {
        MutableLiveData<Option<TimeFrequency>>().apply { value = None }
    }
    val fundFlowTimeFrequencyToSave: LiveData<Option<TimeFrequency>> by lazy { _fundFlowTimeFrequencyToSave }

    private val _selectedRecurrentTransaction by lazy {
        MutableLiveData<Option<RecurrentTransaction>>().apply { value = Option.empty() }
    }
    val selectedRecurrentTransaction: LiveData<Option<RecurrentTransaction>> by lazy { _selectedRecurrentTransaction }

    fun showRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        _fromFund.value =
            DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.source)
        _toFund.value =
            DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.destination)
        _fromLocalDateTime.value = Some(recurrentTransaction.details.recurrence.from)
        _toLocalDateTime.value = Some(recurrentTransaction.details.recurrence.to)
        _fundFlowValue.value = Some(recurrentTransaction.quantification.flow.value)
        _fundFlowTimeFrequency.value = Some(recurrentTransaction.quantification.flow.unit)
    }

    fun setFromFundToSave(maybeFund: Option<Fund>) {
        _fromFundToSave.value = maybeFund
    }

    fun setToFundToSave(maybeFund: Option<Fund>) {
        _toFundToSave.value = maybeFund
    }

    fun setFundFlowToSave(maybeNumber: Option<BigDecimal>) {
        _fundFlowValueToSave.value = maybeNumber
    }

    fun setFundFlowTimeFrequencyToSave(maybeTimeFrequency: Option<TimeFrequency>) {
        _fundFlowTimeFrequencyToSave.value = maybeTimeFrequency
    }

    fun setDropdownFundList(funds: List<Fund>) {
        _dropdownFundContent.value = funds
    }

    fun selectRecurrentTransaction(recurrentTransaction: Option<RecurrentTransaction>) {
        _selectedRecurrentTransaction.value = recurrentTransaction
    }
}
