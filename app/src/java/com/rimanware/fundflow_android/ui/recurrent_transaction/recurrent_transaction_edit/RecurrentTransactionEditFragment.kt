package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import arrow.core.Invalid
import arrow.core.None
import arrow.core.Option
import arrow.core.Valid
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.flatten
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.toOption
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.databinding.FragmentRecurrentTransactionEditBinding
import com.rimanware.fundflow_android.ui.common.NumberRules
import com.rimanware.fundflow_android.ui.common.StringRules
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.valideTimeFrequencyExist
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.FundRules
import com.rimanware.fundflow_android.ui.fund.fund_list.FUND_LIST_VM_KEY
import com.rimanware.fundflow_android.ui.fund.fund_list.FundListViewModelContract
import com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list.RECURRENT_TRANSACTION_LIST_VM_KEY
import com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list.UpdateRecurrentTransactionListViewModelContract
import common.DateTimeInterval
import common.unit.Amount
import common.unit.TimeFrequency
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction
import fundflow.ledgers.RecurrentTransactionDetail
import fundflow.ledgers.RecurrentTransactionQuantification
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import ledger.TransactionCoordinates

class RecurrentTransactionEditFragment :
    ViewBindingFragment<FragmentRecurrentTransactionEditBinding>() {

    private val recurrentTransactionEditViewModel: RecurrentTransactionEditViewModel by viewModels()
    private val fundListViewModelContract: FundListViewModelContract by viewModelContracts(
        FUND_LIST_VM_KEY
    )
    private val recurrentTransactionListViewModelContract: UpdateRecurrentTransactionListViewModelContract by viewModelContracts(
        RECURRENT_TRANSACTION_LIST_VM_KEY
    )

    private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormat = DateTimeFormatter.ISO_LOCAL_TIME

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindView(
            FragmentRecurrentTransactionEditBinding.inflate(
                inflater,
                container,
                false
            )
        )

        val root = viewBinding.root

        recurrentTransactionEditViewModel.selectedRecurrentTransaction.observe(
            viewLifecycleOwner,
            Observer { maybeRecurrentTransaction ->
                maybeRecurrentTransaction.map {
                    recurrentTransactionEditViewModel.showRecurrentTransaction(
                        it
                    )
                }
            })

        // Set dropdown content
        fundListViewModelContract.funds.observe(viewLifecycleOwner, Observer
        {
            recurrentTransactionEditViewModel.setDropdownFundList(it)
        })

        val dropdownFromFund: AutoCompleteTextView = viewBinding.fromFundDropdown
        val dropdownToFund: AutoCompleteTextView = viewBinding.toFundDropdown
        val fundFlowValue: TextInputLayout = viewBinding.textFundFlowValue
        val dropdownTimeFrequency: AutoCompleteTextView = viewBinding.timeFrequencyDropdown
        val fromDateView: TextView = viewBinding.textFromDate
        val toDateView: TextView = viewBinding.textToDate
        val fromTimeView: TextView = viewBinding.textFromTime
        val toTimeView: TextView = viewBinding.textToTime

        // Set content of time frequency dropdown
        val timeFrequencyDropdownAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            TimeFrequency.all.map { it.name }
        )
        dropdownTimeFrequency.setAdapter(timeFrequencyDropdownAdapter)

        // Set content of To and From fund dropdown
        recurrentTransactionEditViewModel.dropdownFundContent.observe(
            viewLifecycleOwner,
            Observer
            { dropdownContent: List<Fund> ->
                val adapter =
                    ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        dropdownContent.map { it.name }
                    )
                dropdownFromFund.setAdapter(adapter)
                dropdownToFund.setAdapter(adapter)
            })

        // Set From fund selection
        recurrentTransactionEditViewModel.fromFund.observe(viewLifecycleOwner,
            Observer
            { maybeFund ->
                maybeFund.map { fund ->
                    dropdownFromFund.setText(fund.name, false)
                }
            })

        // Set To fund selection
        recurrentTransactionEditViewModel.toFund.observe(viewLifecycleOwner,
            Observer
            { maybeFund ->
                maybeFund.map { fund ->
                    dropdownToFund.setText(fund.name, false)
                }
            })

        // Set fund flow amount
        recurrentTransactionEditViewModel.fundFlowValue.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value -> fundFlowValue.editText?.setText("$value") }
            })

        // Set Fund Flow time frequency selection
        recurrentTransactionEditViewModel.fundFlowTimeFrequency.observe(viewLifecycleOwner,
            Observer
            { maybeTimeFrequency ->
                maybeTimeFrequency.map { timeFrequency ->
                    dropdownTimeFrequency.setText(
                        timeFrequency.name,
                        false
                    )
                }
            })

        // Set from date and time
        recurrentTransactionEditViewModel.fromLocalDateTime.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value ->
                    val date: String = value.toLocalDate().format(dateFormat)
                    val time: String = value.toLocalTime().format(timeFormat)
                    fromDateView.text = date
                    fromTimeView.text = time
                }
            })

        // Set to date and time
        recurrentTransactionEditViewModel.toLocalDateTime.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value ->

                    val date: String = value.toLocalDate().format(dateFormat)
                    val time: String = value.toLocalTime().format(timeFormat)
                    toDateView.text = date
                    toTimeView.text = time
                }
            })

        // From Fund Dropdown Selection ---Bind--> ViewModel
        dropdownFromFund.doOnTextChanged { inputText, _, _, _ ->
            when (val result = FundRules.validateFundExists(inputText.toString())) {
                is Invalid -> {
                    dropdownFromFund.error = result.e.message
                    recurrentTransactionEditViewModel.setFromFundToSave(None)
                }
                is Valid -> {
                    dropdownFromFund.error = null
                    recurrentTransactionEditViewModel.setFromFundToSave(result.toOption())
                }
            }
        }

        // To Fund Dropdown Selection ---Bind--> ViewModel
        dropdownToFund.doOnTextChanged { inputText, _, _, _ ->
            when (val result = FundRules.validateFundExists(inputText.toString())) {
                is Invalid -> {
                    dropdownFromFund.error = result.e.message
                    recurrentTransactionEditViewModel.setToFundToSave(None)
                }
                is Valid -> {
                    dropdownFromFund.error = null
                    recurrentTransactionEditViewModel.setToFundToSave(result.toOption())
                }
            }
        }

        // Fundflow amount ---Bind--> ViewModel
        fundFlowValue.editText?.doOnTextChanged { inputText, _, _, _ ->
            when (val stringValidation = StringRules.validateNotEmpty(inputText.toString())) {
                is Invalid -> {
                    fundFlowValue.error = stringValidation.e.message
                    recurrentTransactionEditViewModel.setFundFlowToSave(None)
                }
                is Valid -> {
                    when (val numberValidation =
                        NumberRules.validateBiggerThanZero(BigDecimal(stringValidation.a))) {
                        is Invalid -> {
                            fundFlowValue.error = numberValidation.e.message
                            recurrentTransactionEditViewModel.setFundFlowToSave(None)
                        }
                        is Valid -> {
                            fundFlowValue.error = null
                            recurrentTransactionEditViewModel.setFundFlowToSave(numberValidation.toOption())
                        }
                    }
                }
            }
        }

        // FundFlow Time Frequency Dropdown Selection ---Bind--> ViewModel
        dropdownTimeFrequency.doOnTextChanged { inputText, _, _, _ ->
            when (val result = valideTimeFrequencyExist(inputText.toString())) {
                is Invalid -> {
                    dropdownFromFund.error = result.e.message
                    recurrentTransactionEditViewModel.setFundFlowTimeFrequencyToSave(None)
                }
                is Valid -> {
                    dropdownFromFund.error = null
                    recurrentTransactionEditViewModel.setFundFlowTimeFrequencyToSave(result.toOption())
                }
            }
        }

        val safeArgs: RecurrentTransactionEditFragmentArgs by navArgs()
        val selectedRecurrentTransactionRef: String = safeArgs.selectedRecurrentTransaction

        val selectedRecurrentTransaction =
            maybeSelectedRecurrentTransaction(selectedRecurrentTransactionRef)

        recurrentTransactionEditViewModel.selectRecurrentTransaction(selectedRecurrentTransaction)
        return root
    }

    private fun maybeSelectedRecurrentTransaction(selectedRecurrentTransaction: String): Option<RecurrentTransaction> =
        DataManager.loadRecurrentTransactionUsingRefId(selectedRecurrentTransaction)

    override fun onPause() {
        super.onPause()

        saveRecurrentTransaction(
            recurrentTransactionEditViewModel.selectedRecurrentTransaction.value.toOption()
                .flatten()
        )
    }

    private fun saveRecurrentTransaction(maybeSelectedRecurrentTransaction: Option<RecurrentTransaction>) {

        val maybeFromFundToSave: Option<Fund> =
            recurrentTransactionEditViewModel.fromFundToSave.value.toOption().flatten()
        val maybeSelectedToFund: Option<Fund> =
            recurrentTransactionEditViewModel.toFundToSave.value.toOption().flatten()
        val maybeSelectedFundFlowTimeFrequency: Option<TimeFrequency> =
            recurrentTransactionEditViewModel.fundFlowTimeFrequencyToSave.value.toOption().flatten()
        val maybeFundFlowValue: Option<BigDecimal> =
            recurrentTransactionEditViewModel.fundFlowValueToSave.value.toOption().flatten()

        val fromDateView: TextView = viewBinding.textFromDate
        val selectedFromDate: LocalDate =
            LocalDate.parse(fromDateView.text.toString(), dateFormat)

        val toDateView: TextView = viewBinding.textToDate
        val selectedToDate = LocalDate.parse(toDateView.text.toString(), dateFormat)

        val fromTimeView: TextView = viewBinding.textFromTime
        val selectedFromTime = LocalTime.parse(fromTimeView.text.toString(), timeFormat)

        val toTimeView: TextView = viewBinding.textToTime
        val selectedToTime = LocalTime.parse(toTimeView.text.toString(), timeFormat)

        val selectedFromDateTime = LocalDateTime.of(selectedFromDate, selectedFromTime)
        val selectedToDateTime = LocalDateTime.of(selectedToDate, selectedToTime)

        Option.applicative().map(
            maybeFromFundToSave,
            maybeSelectedToFund,
            maybeSelectedFundFlowTimeFrequency,
            maybeFundFlowValue
        ) { (selectedFromFund, selectedToFund, selectedFundFlowTimeFrequency, fundFlowValue) ->
            maybeSelectedRecurrentTransaction.map { selectedRecurrentTransaction ->
                selectedRecurrentTransaction.copy(
                    quantification = RecurrentTransactionQuantification(
                        Amount(
                            fundFlowValue,
                            selectedFundFlowTimeFrequency
                        )
                    ),
                    details = RecurrentTransactionDetail(
                        DateTimeInterval(
                            selectedFromDateTime,
                            selectedToDateTime
                        )
                    ),
                    transactionCoordinates = TransactionCoordinates(
                        selectedFromFund.reference,
                        selectedToFund.reference
                    )
                )
            }.getOrElse {
                RecurrentTransaction(
                    RecurrentTransactionQuantification(
                        Amount(
                            fundFlowValue,
                            selectedFundFlowTimeFrequency
                        )
                    ),
                    RecurrentTransactionDetail(
                        DateTimeInterval(
                            selectedFromDateTime,
                            selectedToDateTime
                        )
                    ),
                    TransactionCoordinates(
                        selectedFromFund.reference,
                        selectedToFund.reference
                    )
                )
            }
        }.fix()
            .map { recurrentTransactionToSave ->
                DataManager.saveRecurrentTransaction(recurrentTransactionToSave)
                recurrentTransactionListViewModelContract.updateRecurrentTransactionList()
            }
    }
}
