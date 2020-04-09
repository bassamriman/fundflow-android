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
import common.Daily
import common.DateTimeInterval
import common.DateTimeIntervalAPI
import fundflow.DailyFlow
import fundflow.DailyFlowOps
import fundflow.Fund
import fundflow.FundRef
import fundflow.ledgers.RecurrentTransaction
import fundflow.ledgers.RecurrentTransactionDetail
import fundflow.ledgers.RecurrentTransactionQuantification
import ledger.TransactionCoordinates
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class RecurrentTransactionEditFragment : Fragment() {

    private lateinit var recurrentTransactionViewModel: RecurrentTransactionViewModel
    private lateinit var selectedRecurrentTransaction: String
    private val dummyTransaction: RecurrentTransaction = RecurrentTransaction(
        RecurrentTransactionQuantification(flow = DailyFlowOps.ZERO),
        RecurrentTransactionDetail(DateTimeIntervalAPI.infinite()),
        TransactionCoordinates(FundRef(), FundRef())
    )
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")
    private val timeFormat = SimpleDateFormat("hh:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recurrentTransactionViewModel = ViewModelProvider(this).get(RecurrentTransactionViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_fund_edit, container, false)

        //Get the fundListViewModel
        val fundListViewModel = activity?.run {
            ViewModelProvider(this).get(FundListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Set spinner content
        fundListViewModel.funds.observe(this, Observer {
            recurrentTransactionViewModel.setFundList(it)
        })

        val spinnerFromFund = root.findViewById<Spinner>(R.id.spinnerFromFundSelection)
        val spinnerToFund = root.findViewById<Spinner>(R.id.spinnerToFundSelection)
        val fundFlow: TextView = root.findViewById(R.id.textFundFlowValue)
        val fromDateView: TextView = root.findViewById(R.id.textFromDate)
        val toDateView: TextView = root.findViewById(R.id.textToDate)
        val fromTimeView: TextView = root.findViewById(R.id.textFromTime)
        val toTimeView: TextView = root.findViewById(R.id.textToTime)

        recurrentTransactionViewModel.spinnerFundContent.observe(this, Observer { spinnerData ->
            val spinnerAdapter: ArrayAdapter<Fund> =
                ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, spinnerData)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFromFund.adapter = spinnerAdapter
            spinnerToFund.adapter = spinnerAdapter
        })

        recurrentTransactionViewModel.fromFund.observe(this,
            Observer { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = spinnerFromFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> spinnerFromFund.setSelection(position) }
            })

        recurrentTransactionViewModel.toFund.observe(this,
            Observer { maybeFund ->
                val maybePosition: Option<Int> = maybeFund.flatMap { fund ->
                    val adapter: ArrayAdapter<Fund> = spinnerToFund.adapter as ArrayAdapter<Fund>
                    adapter.getPosition(fund).toOption()
                }
                maybePosition.map { position -> spinnerToFund.setSelection(position) }
            })

        recurrentTransactionViewModel.fundFlowValue.observe(this, Observer { maybeValue ->
            maybeValue.map { value -> fundFlow.text = "$value" }
        })

        recurrentTransactionViewModel.fromLocalDateTime.observe(this, Observer { maybeValue ->
            maybeValue.map { value ->
                val date: String = dateFormat.format(value)
                val time: String = timeFormat.format(value)
                fromDateView.text = date
                fromTimeView.text = time
            }
        })

        recurrentTransactionViewModel.toLocalDateTime.observe(this, Observer { maybeValue ->
            maybeValue.map { value ->

                val date: String = dateFormat.format(value)
                val time: String = timeFormat.format(value)
                toDateView.text = date
                toTimeView.text = time
            }
        })

        val safeArgs: RecurrentTransactionEditFragmentArgs by navArgs()
        selectedRecurrentTransaction = safeArgs.selectedRecurrentTransaction

        recurrentTransactionViewModel.selectRecurrentTransaction(
            selectedRecurrentTransaction()
        )

        return root
    }

    private fun selectedRecurrentTransaction(): Option<RecurrentTransaction> =
        DataManager.getRecurrentTransactionByRefId(selectedRecurrentTransaction)

    override fun onPause() {
        super.onPause()
        validateRecurrentTransactionInput(selectedRecurrentTransaction()).map {
            saveRecurrentTransaction(it)
        }
    }

    private fun validateRecurrentTransactionInput(selected: Option<RecurrentTransaction>): Option<RecurrentTransaction> {
        val root = view.toOption()
        return root.map {
            val spinnerFromFund: Spinner = it.findViewById<Spinner>(R.id.spinnerFromFundSelection)
            val selectedFromFund: Fund = spinnerFromFund.selectedItem as Fund

            val spinnerToFund: Spinner = it.findViewById<Spinner>(R.id.spinnerToFundSelection)
            val selectedToFund: Fund = spinnerToFund.selectedItem as Fund

            val fundFlow: TextView = it.findViewById(R.id.textFundFlowValue)
            val fundFlowValue = BigDecimal(fundFlow.text.toString())

            val fromDateView: TextView = it.findViewById(R.id.textFromDate)
            val selectedFromDate: LocalDate =
                dateFormat.parse(fromDateView.text.toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            val toDateView: TextView = it.findViewById(R.id.textToDate)
            val selectedToDate =
                dateFormat.parse(toDateView.text.toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            val fromTimeView: TextView = it.findViewById(R.id.textFromTime)
            val selectedFromTime =
                timeFormat.parse(fromTimeView.text.toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime()

            val toTimeView: TextView = it.findViewById(R.id.textToTime)
            val selectedToTime =
                timeFormat.parse(toTimeView.text.toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime()

            val selectedFromDateTime = LocalDateTime.of(selectedFromDate, selectedFromTime)
            val selectedToDateTime = LocalDateTime.of(selectedToDate, selectedToTime)

            selected.map {
                it.copy(
                    quantification = RecurrentTransactionQuantification(DailyFlow(fundFlowValue, Daily)),
                    details = RecurrentTransactionDetail(DateTimeInterval(selectedFromDateTime, selectedToDateTime)),
                    transactionCoordinates = TransactionCoordinates(
                        selectedFromFund.reference,
                        selectedToFund.reference
                    )
                )
            }.getOrElse {
                RecurrentTransaction(
                    RecurrentTransactionQuantification(DailyFlow(fundFlowValue, Daily)),
                    RecurrentTransactionDetail(DateTimeInterval(selectedFromDateTime, selectedToDateTime)),
                    TransactionCoordinates(selectedFromFund.reference, selectedToFund.reference)
                )
            }
        }
    }

    private fun saveRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        DataManager.addRecurrentTransaction(recurrentTransaction)
    }
}
