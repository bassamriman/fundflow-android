package com.rimanware.fundflow_android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
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

        val adapterFunds: ArrayAdapter<Fund> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, DataManager.funds())
        adapterFunds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFunds.adapter = adapterFunds

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        saveFund(selectedFundOrDefault())
    }

    private fun saveFund(fund: Fund) {
        DataManager.save(fund.copy(name = textFundTitle.text.toString(), description = textFundText.text.toString()))
    }
    
}
