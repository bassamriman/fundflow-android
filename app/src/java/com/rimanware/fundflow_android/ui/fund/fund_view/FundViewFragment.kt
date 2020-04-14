package com.rimanware.fundflow_android.ui.fund.fund_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import fundflow.Fund
import java.math.BigDecimal

class FundViewFragment : Fragment() {

    private lateinit var fundViewViewModel: FundViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fundViewViewModel = ViewModelProvider(this).get(FundViewViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_fund_view, container, false)

        val titleView: TextView = root.findViewById(R.id.textFundTitle)
        val descriptionView: TextView = root.findViewById(R.id.textFundDescriptionValue)

        fundViewViewModel.title.observe(this, Observer { maybeTitle: Option<String> ->
            maybeTitle.map {
                titleView.text = it
            }
        })

        fundViewViewModel.description.observe(this, Observer { maybeDescription: Option<String> ->
            maybeDescription.map {
                descriptionView.text = it
            }
        })
        val inFlowView: TextView = root.findViewById(R.id.textInFlowValue)
        val fundFlowView: TextView = root.findViewById(R.id.textFundFlowValue)
        val outFlowView: TextView = root.findViewById(R.id.textOutFlowValue)

        fundViewViewModel.inFlow.observe(this, Observer { maybeInFlow: Option<BigDecimal> ->
            maybeInFlow.map {
                val test = "$${it.setScale(2)}/Day"
                inFlowView.text = test
            }
        })

        fundViewViewModel.fundFlow.observe(this, Observer { maybeFundFlow: Option<BigDecimal> ->
            maybeFundFlow.map {
                val test = "$${it.setScale(2)}/Day"
                fundFlowView.text = test
            }
        })

        fundViewViewModel.outFlow.observe(this, Observer { maybeOutFlow: Option<BigDecimal> ->
            maybeOutFlow.map {
                val test = "$${it.setScale(2)}/Day"
                outFlowView.text = test
            }
        })

        val safeArgs: FundViewFragmentArgs by navArgs()
        val selectedFund = safeArgs.selectedFund

        fundViewViewModel.selectFund(maybeSelectedFund(selectedFund))

        return root
    }

    private fun maybeSelectedFund(selectedFund: String): Option<Fund> =
        DataManager.loadFundUsingRefId(selectedFund)
}