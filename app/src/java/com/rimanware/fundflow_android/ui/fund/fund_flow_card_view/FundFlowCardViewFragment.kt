package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import arrow.core.*
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.flatten
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.databinding.FragmentFundFlowCardBinding
import com.rimanware.fundflow_android.ui.common.*
import com.rimanware.fundflow_android.ui.core.AppViewModel
import com.rimanware.fundflow_android.ui.core.GlobalDateTimeProviderModelViewContract
import com.rimanware.fundflow_android.ui.fund.fund_view.SelectedFundViewModelContract
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.*

class FundFlowCardViewFragment :
    ViewBindingFragment<FragmentFundFlowCardBinding>() {

    private val fundCardViewViewModel: FundFlowCardViewViewModel by viewModels()
    private val viewModelContract: SelectedFundViewModelContract by viewModelContracts(VM_KEY)
    private val globalDateTimeProviderModelViewContract:
            GlobalDateTimeProviderModelViewContract by viewModelContracts(GLOBAL_VM_KEY)

    private var today: Long = 0
    private var nextMonth: Long = 0
    private var janThisYear: Long = 0
    private var decThisYear: Long = 0
    private var oneYearForward: Long = 0
    private var todayPair: Pair<Long, Long>? = null
    private var nextMonthPair: Pair<Long, Long>? = null

    private fun clearedUTCCalendar(): Calendar {
        val utc =
            Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.clear()
        return utc
    }

    private fun initSettings() {
        today = MaterialDatePicker.thisMonthInUtcMilliseconds()
        val calendar: Calendar = clearedUTCCalendar()
        calendar.timeInMillis = today
        calendar.roll(Calendar.MONTH, 1)
        nextMonth = calendar.timeInMillis
        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.JANUARY
        janThisYear = calendar.timeInMillis
        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.DECEMBER
        decThisYear = calendar.timeInMillis
        calendar.timeInMillis = today
        calendar.roll(Calendar.YEAR, 1)
        oneYearForward = calendar.timeInMillis
        todayPair = Pair<Long, Long>(today, today)
        nextMonthPair = Pair<Long, Long>(nextMonth, nextMonth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindView(FragmentFundFlowCardBinding.inflate(inflater, container, false))

        val root = viewBinding.root

        val launcher: MaterialButton = viewBinding.catDatePickerLaunchButton
        launcher.setOnClickListener {
            initSettings()
            val builder =
                MaterialDatePicker.Builder.datePicker()
            builder.setSelection(today)
            builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)

            val picker: MaterialDatePicker<*> = builder.build()
            picker.addOnPositiveButtonClickListener { selection: Any ->
                val timestamp = selection as Long
                launcher.text = picker.headerText
                fundCardViewViewModel.selectDateTime(
                    Instant.ofEpochMilli(timestamp).atZone(
                        ZoneId.systemDefault()
                    ).toLocalDate().atTime(0, 0)
                )
            }
            picker.show(childFragmentManager, picker.toString())
        }

        val inFlowView: TextView = viewBinding.textInFlowValue
        val fundFlowView: TextView = viewBinding.textFundFlowValue
        val outFlowView: TextView = viewBinding.textOutFlowValue

        fundCardViewViewModel.inFlow.observe(this, Observer { maybeInFlow: Option<BigDecimal> ->
            maybeInFlow.map {
                val test = "$${it.setScale(2)}/Day"
                inFlowView.text = test
            }
        })

        fundCardViewViewModel.fundFlow.observe(this, Observer { maybeFundFlow: Option<BigDecimal> ->
            maybeFundFlow.map {
                val test = "$${it.setScale(2)}/Day"
                fundFlowView.text = test
            }
        })

        fundCardViewViewModel.outFlow.observe(this, Observer { maybeOutFlow: Option<BigDecimal> ->
            maybeOutFlow.map {
                val test = "$${it.setScale(2)}/Day"
                outFlowView.text = test
            }
        })

        viewModelContract.selectedFund.observe(this, Observer {
            fundCardViewViewModel.selectFund(it)
        })

        val selectedDateTimeGlobalTimeTuple =
            combineTuple(
                fundCardViewViewModel.maybeSelectedDateTime,
                globalDateTimeProviderModelViewContract.globalDateTime
            )

        selectedDateTimeGlobalTimeTuple.observe(
            this,
            Observer { maybeSelectedDateTimeGlobalTimeTuple ->
                maybeSelectedDateTimeGlobalTimeTuple.toOption().map {
                    val (maybeSelectedDateTime, maybeGlobalTime) = it
                    maybeSelectedDateTime.map {
                        fundCardViewViewModel.setDateTimeToComputeFlowAt(Some(it))
                    }.getOrElse {
                        maybeGlobalTime.map {
                            fundCardViewViewModel.setDateTimeToComputeFlowAt(Some(it))
                        }.getOrElse {
                            fundCardViewViewModel.setDateTimeToComputeFlowAt(None)
                        }
                    }
                }
            })

        fundCardViewViewModel.computationDateTimeAndFundTuple.observe(
            this,
            Observer { maybeComputationDateTimeAndFundTuple ->
                val maybeFundFlowView = maybeComputationDateTimeAndFundTuple.toOption().map {
                    val (maybeFund, maybeLocalDateTime) = it
                    Option.applicative().map(
                        maybeFund,
                        maybeLocalDateTime
                    ) { (previouslySelectedFund, selectedDateTime) ->
                        val a = DataManager.loadFundFlowView(
                            previouslySelectedFund.reference,
                            selectedDateTime
                        )
                        a
                    }.fix().flatten()
                }.flatten()
                fundCardViewViewModel.selectFunFlowView(maybeFundFlowView)
            })

        fundCardViewViewModel.selectedFundFlowView.observe(
            this,
            Observer { fundCardViewViewModel.showFund(it) })

        return root
    }

    companion object {
        fun <T : SelectedFundViewModelContract> newInstance(contract: Class<T>): FundFlowCardViewFragment {
            return FundFlowCardViewFragment().apply {
                val bundle =
                    Bundle()
                        .registerViewModelContract(VM_KEY, contract)
                        .registerViewModelContract(GLOBAL_VM_KEY, AppViewModel::class.java)
                arguments = bundle
            }
        }
    }
}
