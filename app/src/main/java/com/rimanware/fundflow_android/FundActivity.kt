package com.rimanware.fundflow_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import fundflow.Fund
import kotlinx.android.synthetic.main.activity_fund.*
import kotlinx.android.synthetic.main.content_fund.*

class FundActivity : AppCompatActivity() {
    private var maybeSelectedFundRefId: Option<String> = None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fund)
        setSupportActionBar(toolbar)
        maybeSelectedFundRefId = intent.getStringExtra(EXTRA_FUND_REF_ID).toOption()
        displayFund(selectedFundOrDefault())
    }

    private fun displayFund(fund: Fund) {
        textFundTitle.setText(fund.name)
        textFundText.setText(fund.description)
    }

    private fun selectedFund(): Option<Fund> = maybeSelectedFundRefId.flatMap { DataManager.getFundByRefId(it) }
    private fun selectedFundOrDefault(): Fund =
        selectedFund().getOrElse { Fund.empty() }

    override fun onPause() {
        super.onPause()
        saveFund(selectedFundOrDefault())
    }

    private fun saveFund(fund: Fund) {
        if (textFundTitle.text.toString().isNotEmpty()) {
            DataManager.save(
                fund.copy(
                    name = textFundTitle.text.toString(),
                    description = textFundText.text.toString()
                )
            )
        }

    }

}
