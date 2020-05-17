package com.rimanware.fundflow_android.ui.fund.fund_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import fundflow.Fund

class FundViewViewModel : ViewModel(), SelectedFundViewModelContract {

    private val _selectedFund by lazy {
        MutableLiveData<Option<Fund>>().apply { value = None }
    }
    override val selectedFund: LiveData<Option<Fund>> by lazy { _selectedFund }

    fun selectFund(maybeFund: Option<Fund>) {
        _selectedFund.value = maybeFund
    }

    private val _title by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }
    val title: LiveData<Option<String>> by lazy { _title }

    private val _description by lazy {
        MutableLiveData<Option<String>>().apply { value = None }
    }
    val description: LiveData<Option<String>> by lazy { _description }

    init {
        selectedFund.observeForever { maybeFund: Option<Fund> ->
            maybeFund.map { fund ->
                showFund(fund)
            }
        }
    }

    private fun showFund(fund: Fund) {
        _title.value = Some(fund.name)
        _description.value = Some(fund.description)
    }
}

interface SelectedFundViewModelContract {
    val selectedFund: LiveData<Option<Fund>>
}
