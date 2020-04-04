package com.rimanware.fundflow_android.ui.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rimanware.fundflow_android.R

class FundEditFragment : Fragment() {

    private lateinit var fundEditViewModel: FundEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fundEditViewModel =
            ViewModelProviders.of(this).get(FundEditViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_fund_edit, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        fundEditViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}