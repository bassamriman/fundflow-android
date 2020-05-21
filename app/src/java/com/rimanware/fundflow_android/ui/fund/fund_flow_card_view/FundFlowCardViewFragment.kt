package com.rimanware.fundflow_android.ui.fund.fund_flow_card_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.databinding.FragmentFundFlowCardBinding
import com.rimanware.fundflow_android.ui.common.GLOBAL_VM_KEY
import com.rimanware.fundflow_android.ui.common.VM_KEY
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.combineTuple
import com.rimanware.fundflow_android.ui.common.registerViewModelContract
import com.rimanware.fundflow_android.ui.common.valideTimeFrequencyExist
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.core.AppViewModel
import com.rimanware.fundflow_android.ui.core.GlobalDateTimeProviderModelViewContract
import com.rimanware.fundflow_android.ui.fund.fund_view.SelectedFundViewModelContract
import common.unit.Daily
import common.unit.TimeFrequency
import fundflow.Flow
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

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

        val dropdownTimeFrequency: AutoCompleteTextView = viewBinding.timeFrequencyDropdown
        dropdownTimeFrequency.setText(
            Daily.name,
            false
        )

        // Set content of time frequency dropdown
        val timeFrequencyDropdownAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            TimeFrequency.all().map { it.name }
        )
        dropdownTimeFrequency.setAdapter(timeFrequencyDropdownAdapter)
        // FundFlow Time Frequency Dropdown Selection ---Bind--> ViewModel
        dropdownTimeFrequency.doOnTextChanged { inputText, _, _, _ ->
            when (val result = valideTimeFrequencyExist(inputText.toString())) {
                is Invalid -> {
                    dropdownTimeFrequency.error = result.e.message
                    fundCardViewViewModel.setFundFlowTimeFrequency(Some(Daily))
                }
                is Valid -> {
                    dropdownTimeFrequency.error = null
                    fundCardViewViewModel.setFundFlowTimeFrequency(result.toOption())
                }
            }
        }

        val inFlowView: TextView = viewBinding.textInFlowValue
        val fundFlowView: TextView = viewBinding.textFundFlowValue
        val outFlowView: TextView = viewBinding.textOutFlowValue

        fundCardViewViewModel.inFlow.observe(
            viewLifecycleOwner,
            Observer { maybeInFlow: Option<Flow> ->
                maybeInFlow.map {
                    val test =
                        "$${it.value.setScale(2, RoundingMode.HALF_EVEN)} ${it.unit.perAlias}"
                    inFlowView.text = test
                }
            })

        fundCardViewViewModel.fundFlow.observe(
            viewLifecycleOwner,
            Observer { maybeFundFlow: Option<Flow> ->
                maybeFundFlow.map {
                    val test =
                        "$${it.value.setScale(2, RoundingMode.HALF_EVEN)} ${it.unit.perAlias}"
                    fundFlowView.text = test
                }
            })

        fundCardViewViewModel.outFlow.observe(
            viewLifecycleOwner,
            Observer { maybeOutFlow: Option<Flow> ->
                maybeOutFlow.map {
                    val test =
                        "$${it.value.setScale(2, RoundingMode.HALF_EVEN)} ${it.unit.perAlias}"
                    outFlowView.text = test
                }
            })

        viewModelContract.selectedFund.observe(viewLifecycleOwner, Observer {
            fundCardViewViewModel.selectFund(it)
        })

        val selectedDateTimeGlobalTimeTuple =
            combineTuple(
                fundCardViewViewModel.maybeSelectedDateTime,
                globalDateTimeProviderModelViewContract.globalDateTime
            )

        selectedDateTimeGlobalTimeTuple.observe(
            viewLifecycleOwner,
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

        fundCardViewViewModel.computationDateTimeAndFundTriple.observe(
            viewLifecycleOwner,
            Observer { maybeComputationDateTimeAndFundTriple ->
                val maybeFundFlowView = maybeComputationDateTimeAndFundTriple.toOption().map {
                    val (maybeFund, maybeLocalDateTime, maybeTimeFrequency) = it
                    Option.applicative().map(
                        maybeFund,
                        maybeLocalDateTime,
                        maybeTimeFrequency
                    ) { (previouslySelectedFund, selectedDateTime, timeFrequency) ->
                        DataManager.loadFundFlowView(
                            previouslySelectedFund.reference,
                            selectedDateTime,
                            timeFrequency
                        )
                    }.fix().flatten()
                }.flatten()
                fundCardViewViewModel.selectFunFlowView(maybeFundFlowView)
            })

        fundCardViewViewModel.selectedFundFlowView.observe(
            viewLifecycleOwner,
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
