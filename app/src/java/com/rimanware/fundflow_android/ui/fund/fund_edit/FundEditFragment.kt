package com.rimanware.fundflow_android.ui.fund.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.flatten
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.toOption
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.ui.fund.fund_list.FundListViewModel
import fundflow.Fund

class FundEditFragment : Fragment() {

    private lateinit var fundEditViewModel: FundEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fundEditViewModel = ViewModelProvider(this).get(FundEditViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_fund_edit, container, false)

        val titleView: TextInputLayout = root.findViewById(R.id.textFundTitle)
        val descriptionView: TextInputLayout = root.findViewById(R.id.textFundText)

        fundEditViewModel.titleOfSelectedFund.observe(
            this,
            Observer { maybeTitle: Option<String> ->
                maybeTitle.map { title ->
                    titleView.editText?.setText(title)
                }
            })

        fundEditViewModel.descriptionOfSelectedFund.observe(
            this,
            Observer { maybeDescription: Option<String> ->
                maybeDescription.map { description ->
                    descriptionView.editText?.setText(description)
                }
            })


        val safeArgs: FundEditFragmentArgs by navArgs()
        val selectedFundRef = safeArgs.selectedFund
        val selectedFund = maybeSelectedFund(selectedFundRef)

        titleView.editText?.doOnTextChanged { inputText, _, _, _ ->
            FundEditValidation.handleFundTitleValidation(
                inputText.toString(),
                selectedFund.map { it.name },
                titleView,
                fundEditViewModel
            )
        }
        descriptionView.editText?.doOnTextChanged { inputText, _, _, _ ->
            FundEditValidation.handleFundDescriptionValidation(
                inputText.toString(),
                descriptionView,
                fundEditViewModel
            )
        }

        return root
    }

    private fun maybeSelectedFund(selectedFund: String): Option<Fund> =
        DataManager.loadFundUsingRefId(selectedFund)

    override fun onPause() {
        super.onPause()
        saveFund(fundEditViewModel.selectedFund.value.toOption().flatten())
    }

    private fun saveFund(maybeSelectedFund: Option<Fund>) {
        val maybeValidTitle: Option<String> =
            fundEditViewModel.validTitleInput.value.toOption().flatten()
        val maybeValidDescription: Option<String> =
            fundEditViewModel.validDescriptionInput.value.toOption().flatten()

        Option.applicative().map(
            maybeValidTitle,
            maybeValidDescription
        ) { (validFundTitle, validFundDescription) ->
            maybeSelectedFund.map { selectedFund ->
                selectedFund.copy(
                    name = validFundTitle,
                    description = validFundDescription
                )
            }.getOrElse {
                Fund(validFundTitle, validFundDescription)
            }
        }.fix()
            .map { fundToSave ->
                DataManager.saveFund(fundToSave)
                fundListViewModel().updateFundList()
            }
    }

    fun fundListViewModel(): FundListViewModel = activity?.run {
        ViewModelProvider(this).get(FundListViewModel::class.java)
    } ?: throw Exception("Invalid Activity")
}