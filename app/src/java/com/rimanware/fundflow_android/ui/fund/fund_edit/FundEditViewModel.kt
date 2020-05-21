package com.rimanware.fundflow_android.ui.fund.fund_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import fundflow.Fund
import fundflow.ledgers.RecurrentTransactionFundView

class FundEditViewModel : ViewModel() {

    // Selected Fund Bindings
    private val _selectedFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    val selectedFund: LiveData<Option<Fund>> by lazy { _selectedFund }

    private val _titleOfSelectedFund by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }
    val titleOfSelectedFund: LiveData<Option<String>> by lazy { _titleOfSelectedFund }

    private val _descriptionOfSelectedFund by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }
    val descriptionOfSelectedFund: LiveData<Option<String>> by lazy { _descriptionOfSelectedFund }

    fun showFund(fundView: RecurrentTransactionFundView) {
        _titleOfSelectedFund.value = Some(fundView.fund.name)
        _descriptionOfSelectedFund.value = Some(fundView.fund.description)
    }

    fun selectFund(maybeFund: Option<Fund>) {
        _selectedFund.value = maybeFund
    }

    // User Input
    private val _validTitleToSave by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }

    val validTitleToSave: LiveData<Option<String>> by lazy { _validTitleToSave }

    fun setTitleToSave(maybeTitle: Option<String>) {
        _validTitleToSave.value = maybeTitle
    }

    private val _validDescriptionToSave by lazy {
        MutableLiveData<Option<String>>().apply { value = Some("") }
    }

    val validDescriptionToSave: LiveData<Option<String>> by lazy { _validDescriptionToSave }

    fun setValidDescriptionToSave(maybeDescription: Option<String>) {
        _validDescriptionToSave.value = maybeDescription
    }

    fun clearToSave() {
        _validTitleToSave.value = None
        _validDescriptionToSave.value = None
    }
}
