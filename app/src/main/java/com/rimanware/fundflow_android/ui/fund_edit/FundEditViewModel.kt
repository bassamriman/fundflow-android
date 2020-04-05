package com.rimanware.fundflow_android.ui.fund_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import fundflow.Fund

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

    init {
        selectedFund.observeForever(Observer {
            showFund(it)
        })
    }

    private fun showFund(fund: Fund): Unit {
        _title.value = fund.name
        _description.value = fund.description
    }

    fun selectFund(fund: Fund): Unit {
        _selectedFund.value = fund
    }
}