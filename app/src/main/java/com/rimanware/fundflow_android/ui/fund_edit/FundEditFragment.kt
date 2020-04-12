package com.rimanware.fundflow_android.ui.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.getOrElse
import arrow.core.toOption
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.ui.fund_list.FundListViewModel
import fundflow.Fund

class FundEditFragment : Fragment() {

    private lateinit var fundEditViewModel: FundEditViewModel
    private lateinit var selectedFund: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fundEditViewModel = ViewModelProvider(this).get(FundEditViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_fund_edit, container, false)

        val titleView: TextInputLayout = root.findViewById(R.id.textFundTitle)
        val descriptionView: TextInputLayout = root.findViewById(R.id.textFundText)
        val fundFlowView: TextView = root.findViewById(R.id.textFundFlowValue)
        val inFlowView: TextView = root.findViewById(R.id.textInFlowValue)
        val outFlowView: TextView = root.findViewById(R.id.textOutFlowValue)

        fundEditViewModel.title.observe(this, Observer {
            titleView.editText?.setText(it)
        })
        titleView.editText?.doOnTextChanged { inputText, _, _, _ ->
            FundEditValidation.setValidateFundTitleError(
                inputText.toString(),
                selectedFundOrDefault().name,
                titleView
            )
        }

        fundEditViewModel.description.observe(this, Observer {
            descriptionView.editText?.setText(it)
        })
        descriptionView.editText?.doOnTextChanged { inputText, _, _, _ ->
            FundEditValidation.setValidateFundDescriptionError(
                inputText.toString(),
                descriptionView
            )
        }

        fundEditViewModel.fundFlow.observe(this, Observer {
            fundFlowView.text = "$it"
        })

        fundEditViewModel.inFlow.observe(this, Observer {
            inFlowView.text = "$it"
        })

        fundEditViewModel.outFlow.observe(this, Observer {
            outFlowView.text = "$it"
        })

        val safeArgs: FundEditFragmentArgs by navArgs()
        selectedFund = safeArgs.selectedFund

        fundEditViewModel.selectFund(getFundOrDefault(selectedFund))

        return root
    }

    private fun selectedFundOrDefault(): Fund =
        getFundOrDefault(selectedFund)

    private fun getFundOrDefault(id: String): Fund =
        DataManager.loadFundUsingRefId(id).getOrElse { Fund.empty() }

    override fun onPause() {
        super.onPause()
        saveFund(selectedFundOrDefault())
    }

    private fun saveFund(fund: Fund) {
        val root = view.toOption()
        root.map {
            val titleView: TextInputLayout = it.findViewById(R.id.textFundTitle)
            val descriptionView: TextInputLayout = it.findViewById(R.id.textFundText)
            val title = titleView.editText?.text.toString()
            val description = descriptionView.editText?.text.toString()
            StringRules.validateNotEmpty(title).toOption().map { nonEmptyTitle: String ->
                FundEditValidation.validateFundTitle(nonEmptyTitle, selectedFundOrDefault().name)
                    .toOption()
                    .map { validFundTitle: String ->
                        FundEditValidation.validateFundDescription(description).toOption()
                            .map { validFundDescription: String ->
                                DataManager.saveFund(
                                    fund.copy(
                                        name = validFundTitle,
                                        description = validFundDescription
                                    )
                                )
                                val fundListViewModel =
                                    activity?.run {
                                        ViewModelProvider(this).get(FundListViewModel::class.java)
                                    } ?: throw Exception("Invalid Activity")
                                fundListViewModel.updateFundList()
                            }
                    }
            }
        }


    }
}