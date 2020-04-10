package com.rimanware.fundflow_android.ui.recurrent_transaction_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.ui.fund_list.FundListViewModel
import com.rimanware.fundflow_android.ui.recurrent_transaction_list.RecurrentTransactionListViewModel
import common.Daily
import common.DateTimeInterval
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

class RecurrentTransactionEditFragment : Fragment() {

    private lateinit var recurrentTransactionEditViewModel: RecurrentTransactionEditViewModel
    private lateinit var selectedRecurrentTransaction: String

    private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormat = DateTimeFormatter.ISO_LOCAL_TIME

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recurrentTransactionEditViewModel =
            ViewModelProvider(this).get(RecurrentTransactionEditViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_recurrent_transaction_edit, container, false)

        //Get the fundListViewModel
        val fundListViewModel = activity?.run {
            ViewModelProvider(this).get(FundListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Set spinner content
        fundListViewModel.funds.observe(this, Observer {
            recurrentTransactionEditViewModel.setFundList(it)
        })

        val spinnerFromFund = root.findViewById<Spinner>(R.id.spinnerFromFundSelection)
        val spinnerToFund = root.findViewById<Spinner>(R.id.spinnerToFundSelection)
        val fundFlow: TextView = root.findViewById(R.id.textFundFlowValue)
        val fromDateView: TextView = root.findViewById(R.id.textFromDate)
        val toDateView: TextView = root.findViewById(R.id.textToDate)
        val fromTimeView: TextView = root.findViewById(R.id.textFromTime)
        val toTimeView: TextView = root.findViewById(R.id.textToTime)

        recurrentTransactionEditViewModel.spinnerFundContent.observe(this, Observer { spinnerData ->
            val spinnerAdapter: ArrayAdapter<Fund> =
                ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, spinnerData)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFromFund.adapter = spinnerAdapter
            spinnerToFund.adapter = spinnerAdapter
        })

        recurrentTransactionEditViewModel.fromFund.observe(this,
            Observer { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = spinnerFromFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> spinnerFromFund.setSelection(position) }
            })

        recurrentTransactionEditViewModel.toFund.observe(this,
            Observer { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = spinnerToFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> spinnerToFund.setSelection(position) }
            })

        recurrentTransactionEditViewModel.fundFlowValue.observe(this, Observer { maybeValue ->
            maybeValue.map { value -> fundFlow.text = "$value" }
        })

        recurrentTransactionEditViewModel.fromLocalDateTime.observe(this, Observer { maybeValue ->
            maybeValue.map { value ->
                val date: String = value.toLocalDate().format(dateFormat)
                val time: String = value.toLocalTime().format(timeFormat)
                fromDateView.text = date
                fromTimeView.text = time
            }
        })

        recurrentTransactionEditViewModel.toLocalDateTime.observe(this, Observer { maybeValue ->
            maybeValue.map { value ->

                val date: String = value.toLocalDate().format(dateFormat)
                val time: String = value.toLocalTime().format(timeFormat)
                toDateView.text = date
                toTimeView.text = time
            }
        })

        val safeArgs: RecurrentTransactionEditFragmentArgs by navArgs()
        selectedRecurrentTransaction = safeArgs.selectedRecurrentTransaction

        recurrentTransactionEditViewModel.selectRecurrentTransaction(
            selectedRecurrentTransaction()
        )

        return root
    }

    private fun selectedRecurrentTransaction(): Option<RecurrentTransaction> =
        DataManager.loadRecurrentTransactionUsingRefId(selectedRecurrentTransaction)

    override fun onPause() {
        super.onPause()
        validateRecurrentTransactionInput(selectedRecurrentTransaction()).map {
            saveRecurrentTransaction(it)
        }
    }

    private fun validateRecurrentTransactionInput(selected: Option<RecurrentTransaction>): Option<RecurrentTransaction> {
        val root = view.toOption()
        return root.flatMap {
            val spinnerFromFund: Spinner = it.findViewById<Spinner>(R.id.spinnerFromFundSelection)
            val selectedFromFund: Option<Fund> =
                spinnerFromFund.selectedItem.toOption().map { it as Fund }

            val spinnerToFund: Spinner = it.findViewById<Spinner>(R.id.spinnerToFundSelection)
            val selectedToFund: Option<Fund> =
                spinnerToFund.selectedItem.toOption().map { it as Fund }

            selectedFromFund.flatMap { fromFund: Fund ->
                selectedToFund.map { toFund: Fund ->

                    val fundFlow: TextView = it.findViewById(R.id.textFundFlowValue)
                    val fundFlowValue = BigDecimal(fundFlow.text.toString())

                    val fromDateView: TextView = it.findViewById(R.id.textFromDate)
                    val selectedFromDate: LocalDate =
                        LocalDate.parse(fromDateView.text.toString(), dateFormat)

                    val toDateView: TextView = it.findViewById(R.id.textToDate)
                    val selectedToDate = LocalDate.parse(toDateView.text.toString(), dateFormat)

                    val fromTimeView: TextView = it.findViewById(R.id.textFromTime)
                    val selectedFromTime = LocalTime.parse(fromTimeView.text.toString(), timeFormat)

                    val toTimeView: TextView = it.findViewById(R.id.textToTime)
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
                            RecurrentTransactionQuantification(DailyFlow(fundFlowValue, Daily)),
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
        val recurrentTransactionViewModel =
            activity?.run {
                ViewModelProvider(this).get(RecurrentTransactionListViewModel::class.java)
            } ?: throw Exception("Invalid Activity")

        recurrentTransactionViewModel.updateRecurrentTransactionList()
    }
}
