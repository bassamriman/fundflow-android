package com.rimanware.fundflow_android

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import fundflow.Fund
import kotlinx.android.synthetic.main.activity_fund.*
import kotlinx.android.synthetic.main.content_fund.*


class FundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fund)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val activityIntent = Intent(this, MainActivity::class.java)
            startActivity(activityIntent)
        }

        listFunds.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                DataManager.funds()
            )

        listFunds.setOnItemClickListener { parent, view, position, id ->
            val activityIntent = Intent(this, MainActivity::class.java)
            activityIntent.putExtra(EXTRA_FUND_REF_ID, DataManager.funds()[id.toInt()].reference.id)
            startActivity(activityIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = (listFunds.adapter as ArrayAdapter<Fund>)
        adapter.setNotifyOnChange(false)
        adapter.clear()
        for (item in DataManager.funds()) adapter.add(item)
        adapter.setNotifyOnChange(true)
        adapter.notifyDataSetChanged()
    }
}
