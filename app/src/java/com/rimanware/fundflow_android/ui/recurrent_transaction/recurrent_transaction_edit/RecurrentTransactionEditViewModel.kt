package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.toOption
import com.rimanware.fundflow_android.DataManager
import common.unit.TimeFrequency
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class RecurrentTransactionEditViewModel : ViewModel() {

    // Recurrent Transaction fund dropdown content
    private val _dropdownFundContent by lazy {
        MutableLiveData<List<Fund>>().apply { value = DataManager.loadAllFunds() }
    }
    val dropdownFundContent: LiveData<List<Fund>> by lazy { _dropdownFundContent }

    fun setDropdownFundList(funds: List<Fund>) {
        _dropdownFundContent.value = funds
    }

    // Selected Recurrent Transactions
    private val _fromFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFund: LiveData<Option<Fund>> by lazy { _fromFund }

    private val _toFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFund: LiveData<Option<Fund>> by lazy { _toFund }

    private val _fromDateTime by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = LocalDateTime.now().toOption() }
    }
    val fromDateTime: LiveData<Option<LocalDateTime>> by lazy { _fromDateTime }

    private val _toDateTime by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = LocalDateTime.now().toOption() }
    }
    val toDateTime: LiveData<Option<LocalDateTime>> by lazy { _toDateTime }

    private val _fundFlowValue by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val fundFlowValue: LiveData<Option<BigDecimal>> by lazy { _fundFlowValue }

    private val _fundFlowTimeFrequency by lazy {
        MutableLiveData<Option<TimeFrequency>>().apply { value = None }
    }
    val fundFlowTimeFrequency: LiveData<Option<TimeFrequency>> by lazy { _fundFlowTimeFrequency }

    private val _selectedRecurrentTransaction by lazy {
        MutableLiveData<Option<RecurrentTransaction>>().apply { value = Option.empty() }
    }
    val selectedRecurrentTransaction: LiveData<Option<RecurrentTransaction>> by lazy { _selectedRecurrentTransaction }

    fun showSelectedRecurrentTransaction(recurrentTransaction: Option<RecurrentTransaction>) {
        recurrentTransaction.map {
            _fromFund.value =
                DataManager.loadFundUsingRef(it.transactionCoordinates.source)
            _toFund.value =
                DataManager.loadFundUsingRef(it.transactionCoordinates.destination)
            _fromDateTime.value = Some(it.details.recurrence.from)
            _toDateTime.value = Some(it.details.recurrence.to)
            _fundFlowValue.value = Some(it.quantification.flow.value)
            _fundFlowTimeFrequency.value = Some(it.quantification.flow.unit)
        }.getOrElse {
            _fromFund.value = None
            _toFund.value = None
            _fromDateTime.value = LocalDateTime.now().toOption()
            _toDateTime.value = LocalDateTime.now().toOption()
            _fundFlowValue.value = None
            _fundFlowTimeFrequency.value = None
            resetToSave()
        }
    }

    fun selectRecurrentTransaction(recurrentTransaction: Option<RecurrentTransaction>) {
        _selectedRecurrentTransaction.value = recurrentTransaction
    }

    // Recurrent Transaction to save
    private val _fromFundToSave by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val fromFundToSave: LiveData<Option<Fund>> by lazy { _fromFundToSave }

    private val _toFundToSave by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val toFundToSave: LiveData<Option<Fund>> by lazy { _toFundToSave }

    private val _fundFlowValueToSave by lazy {
        MutableLiveData<Option<BigDecimal>>().apply { value = Some(BigDecimal.ZERO) }
    }
    val fundFlowValueToSave: LiveData<Option<BigDecimal>> by lazy { _fundFlowValueToSave }

    private val _fundFlowTimeFrequencyToSave by lazy {
        MutableLiveData<Option<TimeFrequency>>().apply { value = None }
    }
    val fundFlowTimeFrequencyToSave: LiveData<Option<TimeFrequency>> by lazy { _fundFlowTimeFrequencyToSave }

    // From date & time to save
    private val _fromDateToSave by lazy {
        MutableLiveData<Option<LocalDate>>().apply { value = LocalDate.now().toOption() }
    }
    val fromDateToSave: LiveData<Option<LocalDate>> by lazy { _fromDateToSave }

    private val _fromTimeToSave by lazy {
        MutableLiveData<Option<LocalTime>>().apply { value = LocalTime.now().toOption() }
    }
    val fromTimeToSave: LiveData<Option<LocalTime>> by lazy { _fromTimeToSave }

    private val _fromDateTimeToSave by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val fromDateTimeToSave: LiveData<Option<LocalDateTime>> by lazy { _fromDateTimeToSave }

    // To date & time to save
    private val _toDateToSave by lazy {
        MutableLiveData<Option<LocalDate>>().apply { value = LocalDate.now().toOption() }
    }
    val toDateToSave: LiveData<Option<LocalDate>> by lazy { _toDateToSave }

    private val _toTimeToSave by lazy {
        MutableLiveData<Option<LocalTime>>().apply { value = LocalTime.now().toOption() }
    }
    val toTimeToSave: LiveData<Option<LocalTime>> by lazy { _toTimeToSave }

    private val _toDateTimeToSave by lazy {
        MutableLiveData<Option<LocalDateTime>>().apply { value = None }
    }
    val toDateTimeToSave: LiveData<Option<LocalDateTime>> by lazy { _toDateTimeToSave }

    // Set Recurrent Transaction parameters
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

    // Set from date & time
    fun setFromDateToSave(maybeLocalDate: Option<LocalDate>) {
        _fromDateToSave.value = maybeLocalDate
    }

    fun setFromTimeToSave(maybeLocalTime: Option<LocalTime>) {
        _fromTimeToSave.value = maybeLocalTime
    }

    fun setFromDateTimeToSave(maybeLocalDateTime: Option<LocalDateTime>) {
        _fromDateTimeToSave.value = maybeLocalDateTime
    }

    // Set to date & time
    fun setToDateToSave(maybeLocalDate: Option<LocalDate>) {
        _toDateToSave.value = maybeLocalDate
    }

    fun setToTimeToSave(maybeLocalTime: Option<LocalTime>) {
        _toTimeToSave.value = maybeLocalTime
    }

    fun setToDateTimeToSave(maybeLocalDateTime: Option<LocalDateTime>) {
        _toDateTimeToSave.value = maybeLocalDateTime
    }

    fun resetToSave() {
        _fromFundToSave.value = None
        _toFundToSave.value = None
        _fundFlowValueToSave.value = None
        _fundFlowTimeFrequencyToSave.value = None
        _fromDateToSave.value = None
        _fromTimeToSave.value = None
        _fromDateTimeToSave.value = None
        _toDateToSave.value = None
        _toTimeToSave.value = None
        _toDateTimeToSave.value = None
    }
}
