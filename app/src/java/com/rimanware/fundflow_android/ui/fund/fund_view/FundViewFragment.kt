package com.rimanware.fundflow_android.ui.fund.fund_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.databinding.FragmentFundViewBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_flow_card_view.FundFlowCardViewFragment
import fundflow.Fund


class FundViewFragment : ViewBindingFragment<FragmentFundViewBinding>() {

    private val fundViewViewModel: FundViewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindView(
            FragmentFundViewBinding.inflate(
                inflater,
                container,
                false
            )
        )

        val root = viewBinding.root

        val titleView: TextView = viewBinding.textFundTitle
        val descriptionView: TextView = viewBinding.textFundDescriptionValue

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

        val safeArgs: FundViewFragmentArgs by navArgs()
        val selectedFund = safeArgs.selectedFund

        fundViewViewModel.selectFund(maybeSelectedFund(selectedFund))

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fundFlowCardViewFragment: Fragment =
            FundFlowCardViewFragment.newInstance(FundViewViewModel::class.java)
        childFragmentManager.beginTransaction()
            .replace(R.id.fundFlowCardFragment, fundFlowCardViewFragment).commit()
    }

    private fun maybeSelectedFund(selectedFund: String): Option<Fund> =
        DataManager.loadFundUsingRefId(selectedFund)
}