package com.rimanware.fundflow_android.ui.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.getOrElse
import arrow.core.toOption
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

        val titleView: TextView = root.findViewById(R.id.textFundTitle)
        val descriptionView: TextView = root.findViewById(R.id.textFundText)
        val fundFlowView: TextView = root.findViewById(R.id.textFundFlowValue)
        val inFlowView: TextView = root.findViewById(R.id.textInFlowValue)
        val outFlowView: TextView = root.findViewById(R.id.textOutFlowValue)

        fundEditViewModel.title.observe(this, Observer {
            titleView.text = it
        })

        fundEditViewModel.description.observe(this, Observer {
            descriptionView.text = it
        })

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
            val titleView: TextView = it.findViewById(R.id.textFundTitle)
            val descriptionView: TextView = it.findViewById(R.id.textFundText)
            if (titleView.text.toString().isNotEmpty()) {
                DataManager.saveFund(
                    fund.copy(
                        name = titleView.text.toString(),
                        description = descriptionView.text.toString()
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