package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import arrow.core.Invalid
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
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
import com.rimanware.fundflow_android.ui.common.DateTimeRules
import com.rimanware.fundflow_android.ui.common.NumberRules
import com.rimanware.fundflow_android.ui.common.StringRules
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.combineTuple
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

    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
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
            Observer {
                recurrentTransactionEditViewModel.showSelectedRecurrentTransaction(it)
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

        val fromDateView: TextInputLayout = viewBinding.textFromDate
        val fromTimeView: TextInputLayout = viewBinding.textFromTime

        val toDateView: TextInputLayout = viewBinding.textToDate
        val toTimeView: TextInputLayout = viewBinding.textToTime

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
        recurrentTransactionEditViewModel.fromDateTime.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value ->
                    val date: String = value.toLocalDate().format(dateFormat)
                    val time: String = value.toLocalTime().format(timeFormat)
                    fromDateView.editText?.setText(date)
                    fromTimeView.editText?.setText(time)
                }
            })

        // Set to date and time
        recurrentTransactionEditViewModel.toDateTime.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value ->
                    val date: String = value.toLocalDate().format(dateFormat)
                    val time: String = value.toLocalTime().format(timeFormat)
                    toDateView.editText?.setText(date)
                    toTimeView.editText?.setText(time)
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
                    dropdownToFund.error = result.e.message
                    recurrentTransactionEditViewModel.setToFundToSave(None)
                }
                is Valid -> {
                    dropdownToFund.error = null
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
                    dropdownTimeFrequency.error = result.e.message
                    recurrentTransactionEditViewModel.setFundFlowTimeFrequencyToSave(None)
                }
                is Valid -> {
                    dropdownTimeFrequency.error = null
                    recurrentTransactionEditViewModel.setFundFlowTimeFrequencyToSave(result.toOption())
                }
            }
        }

        // From Date Selection ---Bind--> ViewModel
        fromDateView.editText?.doOnTextChanged { inputText, _, _, _ ->
            when (val result = DateTimeRules.validateIsDate(dateFormat)(inputText.toString())) {
                is Invalid -> {
                    fromDateView.error = result.e.message
                    recurrentTransactionEditViewModel.setFromDateToSave(None)
                }
                is Valid -> {
                    fromDateView.error = null
                    recurrentTransactionEditViewModel.setFromDateToSave(result.toOption())
                }
            }
        }

        // From Time Selection ---Bind--> ViewModel
        fromTimeView.editText?.doOnTextChanged { inputText, _, _, _ ->
            when (val result = DateTimeRules.validateIsTime(timeFormat)(inputText.toString())) {
                is Invalid -> {
                    fromTimeView.error = result.e.message
                    recurrentTransactionEditViewModel.setFromTimeToSave(None)
                }
                is Valid -> {
                    fromTimeView.error = null
                    recurrentTransactionEditViewModel.setFromTimeToSave(result.toOption())
                }
            }
        }

        // To Date Selection ---Bind--> ViewModel
        toDateView.editText?.doOnTextChanged { inputText, _, _, _ ->
            when (val result = DateTimeRules.validateIsDate(dateFormat)(inputText.toString())) {
                is Invalid -> {
                    toDateView.error = result.e.message
                    recurrentTransactionEditViewModel.setToDateToSave(None)
                }
                is Valid -> {
                    toDateView.error = null
                    recurrentTransactionEditViewModel.setToDateToSave(result.toOption())
                }
            }
        }

        // To Time Selection ---Bind--> ViewModel
        toTimeView.editText?.doOnTextChanged { inputText, _, _, _ ->
            when (val result = DateTimeRules.validateIsTime(timeFormat)(inputText.toString())) {
                is Invalid -> {
                    toTimeView.error = result.e.message
                    recurrentTransactionEditViewModel.setToTimeToSave(None)
                }
                is Valid -> {
                    toTimeView.error = null
                    recurrentTransactionEditViewModel.setToTimeToSave(result.toOption())
                }
            }
        }

        val fromDateTimeToSaveTuple: LiveData<Pair<Option<LocalDate>, Option<LocalTime>>> =
            combineTuple(
                recurrentTransactionEditViewModel.fromDateToSave,
                recurrentTransactionEditViewModel.fromTimeToSave
            )
        fromDateTimeToSaveTuple.observe(
            viewLifecycleOwner,
            Observer { maybeFromDateTimeToSaveTuple ->
                maybeFromDateTimeToSaveTuple.toOption().map {
                    val (maybeFromDateToSave: Option<LocalDate>, maybeFromTimeToSave: Option<LocalTime>) = it
                    Option.applicative().map(
                        maybeFromDateToSave,
                        maybeFromTimeToSave
                    ) { (fromDateToSave, fromTimeToSave) ->
                        recurrentTransactionEditViewModel.setFromDateTimeToSave(
                            Some(
                                LocalDateTime.of(
                                    fromDateToSave,
                                    fromTimeToSave
                                )
                            )
                        )
                    }.fix()
                }
            })

        val toDateTimeToSaveTuple: LiveData<Pair<Option<LocalDate>, Option<LocalTime>>> =
            combineTuple(
                recurrentTransactionEditViewModel.toDateToSave,
                recurrentTransactionEditViewModel.toTimeToSave
            )
        toDateTimeToSaveTuple.observe(
            viewLifecycleOwner,
            Observer { maybeToDateTimeToSaveTuple ->
                maybeToDateTimeToSaveTuple.toOption().map {
                    val (maybeToDateToSave: Option<LocalDate>, maybeToTimeToSave: Option<LocalTime>) = it
                    Option.applicative().map(
                        maybeToDateToSave,
                        maybeToTimeToSave
                    ) { (toDateToSave: LocalDate, toTimeToSave: LocalTime) ->
                        recurrentTransactionEditViewModel.setToDateTimeToSave(
                            Some(
                                LocalDateTime.of(
                                    toDateToSave,
                                    toTimeToSave
                                )
                            )
                        )
                    }.fix()
                }
            })

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
        val maybeToFundToSave: Option<Fund> =
            recurrentTransactionEditViewModel.toFundToSave.value.toOption().flatten()

        val maybeFundFlowTimeFrequencyToSave: Option<TimeFrequency> =
            recurrentTransactionEditViewModel.fundFlowTimeFrequencyToSave.value.toOption().flatten()
        val maybeFundFlowValueToSave: Option<BigDecimal> =
            recurrentTransactionEditViewModel.fundFlowValueToSave.value.toOption().flatten()

        val maybeFromDateTimeToSave: Option<LocalDateTime> =
            recurrentTransactionEditViewModel.fromDateTimeToSave.value.toOption().flatten()
        val maybeToDateTimeToSave: Option<LocalDateTime> =
            recurrentTransactionEditViewModel.toDateTimeToSave.value.toOption().flatten()

        Option.applicative().map(
            maybeFromFundToSave,
            maybeToFundToSave,
            maybeFundFlowTimeFrequencyToSave,
            maybeFundFlowValueToSave,
            maybeFromDateTimeToSave,
            maybeToDateTimeToSave
        ) { (selectedFromFund, selectedToFund, selectedFundFlowTimeFrequency, fundFlowValue, fromDateTimeToSave, toDateTimeToSave) ->
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
                            fromDateTimeToSave,
                            toDateTimeToSave
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
                            fromDateTimeToSave,
                            toDateTimeToSave
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
