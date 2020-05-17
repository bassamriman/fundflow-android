package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import arrow.core.extensions.option.monad.flatten
import arrow.core.getOrElse
import arrow.core.toOption
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.databinding.FragmentRecurrentTransactionEditBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_list.FUND_LIST_VM_KEY
import com.rimanware.fundflow_android.ui.fund.fund_list.FundListViewModelContract
import com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list.RECURRENT_TRANSACTION_LIST_VM_KEY
import com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list.UpdateRecurrentTransactionListViewModelContract
import common.DateTimeInterval
import common.unit.Daily
import fundflow.DailyFlow
import fundflow.Fund
import fundflow.ledgers.RecurrentTransaction
import fundflow.ledgers.RecurrentTransactionDetail
import fundflow.ledgers.RecurrentTransactionQuantification
import ledger.TransactionCoordinates
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        val fundFlow: TextInputLayout = viewBinding.textFundFlowValue
        val dropdownFrequency: AutoCompleteTextView = viewBinding.timeFrequencyDropdown
        val fromDateView: TextView = viewBinding.textFromDate
        val toDateView: TextView = viewBinding.textToDate
        val fromTimeView: TextView = viewBinding.textFromTime
        val toTimeView: TextView = viewBinding.textToTime


        val COUNTRIES =
            arrayOf("Item 1", "Item 2", "Item 3", "Item 4")

        val timeFrequencyDropdownAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            COUNTRIES
        )
        dropdownFrequency.setAdapter(timeFrequencyDropdownAdapter)

        //Set content of To and From fund dropdown
        recurrentTransactionEditViewModel.dropdownFundContent.observe(
            viewLifecycleOwner,
            Observer
            { dropdownContent: List<Fund> ->
                val adapter =
                    ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        dropdownContent
                    )
                dropdownFromFund.setAdapter(adapter)
                dropdownToFund.setAdapter(adapter)
            })

        //Set From fund selection
        recurrentTransactionEditViewModel.fromFund.observe(viewLifecycleOwner,
            Observer
            { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = dropdownFromFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> dropdownFromFund.setSelection(position) }
            })

        //Set To fund selection
        recurrentTransactionEditViewModel.toFund.observe(viewLifecycleOwner,
            Observer
            { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = dropdownToFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> dropdownToFund.setSelection(position) }
            })

        //Set fund flow amount
        recurrentTransactionEditViewModel.fundFlowValue.observe(
            viewLifecycleOwner,
            Observer
            { maybeValue ->
                maybeValue.map { value -> fundFlow.editText?.setText("$value") }
            })

        //Set from date and time
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

        //Set to date and time
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
        validateRecurrentTransactionInput(
            recurrentTransactionEditViewModel.selectedRecurrentTransaction.value.toOption()
                .flatten()
        ).map {
            saveRecurrentTransaction(it)
        }
    }

    private fun validateRecurrentTransactionInput(selected: Option<RecurrentTransaction>): Option<RecurrentTransaction> {
        val root = view.toOption()
        return root.flatMap {
            val dropdownFromFund = viewBinding.fromFundDropdown

            val selectedFromFund: Option<Fund> =
                dropdownFromFund.text.toOption().map { it as Fund }

            val dropdownToFund = viewBinding.toFundDropdown
            val selectedToFund: Option<Fund> =
                dropdownToFund.text.toOption().map { it as Fund }

            selectedFromFund.flatMap { fromFund: Fund ->
                selectedToFund.map { toFund: Fund ->

                    val fundFlow: TextInputLayout = viewBinding.textFundFlowValue
                    val fundFlowValue = BigDecimal(fundFlow.editText?.text.toString())

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

                    selected.map {
                        it.copy(
                            quantification = RecurrentTransactionQuantification(
                                DailyFlow(
                                    fundFlowValue,
                                    Daily
                                )
                            ),
                            details = RecurrentTransactionDetail(
                                DateTimeInterval(
                                    selectedFromDateTime,
                                    selectedToDateTime
                                )
                            ),
                            transactionCoordinates = TransactionCoordinates(
                                fromFund.reference,
                                toFund.reference
                            )
                        )
                    }.getOrElse {
                        RecurrentTransaction(
                            RecurrentTransactionQuantification(
                                DailyFlow(
                                    fundFlowValue,
                                    Daily
                                )
                            ),
                            RecurrentTransactionDetail(
                                DateTimeInterval(
                                    selectedFromDateTime,
                                    selectedToDateTime
                                )
                            ),
                            TransactionCoordinates(
                                fromFund.reference,
                                toFund.reference
                            )
                        )
                    }
                }
            }
        }
    }

    private fun saveRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        DataManager.saveRecurrentTransaction(recurrentTransaction)
        recurrentTransactionListViewModelContract.updateRecurrentTransactionList()
    }
}
