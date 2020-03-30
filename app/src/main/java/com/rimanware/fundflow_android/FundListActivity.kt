package com.rimanware.fundflow_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_fund_list.*
import kotlinx.android.synthetic.main.content_fund_list.*


class FundListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fund_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(this, FundActivity::class.java))
        }

        listItems.layoutManager = LinearLayoutManager(this)

        listItems.adapter = FundRecyclerAdapter(this, DataManager.funds())

    }

    override fun onResume() {
        super.onResume()
    }
}
