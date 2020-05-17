package com.rimanware.fundflow_android.ui.fund.fund_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.rimanware.fundflow_android.DataManager
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

    init {
        selectedFund.observeForever { maybeFund ->
            maybeFund.map { fund: Fund ->
                DataManager.loadFundView(fund.reference).map { showFund(it) }
            }
        }
    }

    private fun showFund(fundView: RecurrentTransactionFundView) {
        _titleOfSelectedFund.value = Some(fundView.fund.name)
        _descriptionOfSelectedFund.value = Some(fundView.fund.description)
    }

    fun selectFund(maybeFund: Option<Fund>) {
        _selectedFund.value = maybeFund
    }

    // User Input
    private val _validTitleInput by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }

    val validTitleInput: LiveData<Option<String>> by lazy { _validTitleInput }

    fun setTitleInput(maybeTitle: Option<String>) {
        _validTitleInput.value = maybeTitle
    }

    private val _validDescriptionInput by lazy {
        MutableLiveData<Option<String>>().apply { value = Some("") }
    }

    val validDescriptionInput: LiveData<Option<String>> by lazy { _validDescriptionInput }

    fun setValidDescriptionInput(maybeDescription: Option<String>) {
        _validDescriptionInput.value = maybeDescription
    }
}
