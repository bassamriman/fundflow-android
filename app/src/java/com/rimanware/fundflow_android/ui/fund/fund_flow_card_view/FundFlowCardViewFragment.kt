package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import arrow.core.Option
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.rimanware.fundflow_android.databinding.FragmentFundFlowCardBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.registerViewModelContract
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_view.SelectedFundViewModelContract
import java.math.BigDecimal
import java.util.*

class FundFlowCardViewFragment() :
    ViewBindingFragment<FragmentFundFlowCardBinding>() {

    private val fundCardViewViewModel: FundFlowCardViewViewModel by viewModels()
    private val viewModelContract: SelectedFundViewModelContract by viewModelContracts()

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
                launcher.text = picker.headerText
            }
            picker.show(parentFragmentManager, picker.toString())
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

        return root
    }

    companion object {
        fun <T : SelectedFundViewModelContract> newInstance(contract: Class<T>): FundFlowCardViewFragment {
            return FundFlowCardViewFragment().apply {
                val bundle = registerViewModelContract(contract)
                arguments = bundle
            }
        }
    }
}