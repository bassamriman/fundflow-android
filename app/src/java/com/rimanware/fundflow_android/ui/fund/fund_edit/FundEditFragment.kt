package com.rimanware.fundflow_android.ui.fund.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import arrow.core.Option
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.flatten
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.toOption
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.databinding.FragmentFundEditBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_list.FUND_LIST_VM_KEY
import com.rimanware.fundflow_android.ui.fund.fund_list.UpdateFundListViewModelContract
import fundflow.Fund

class FundEditFragment : ViewBindingFragment<FragmentFundEditBinding>() {

    private val fundEditViewModel: FundEditViewModel by viewModels()
    private val modelViewContract: UpdateFundListViewModelContract by viewModelContracts(
        FUND_LIST_VM_KEY
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = bindView(FragmentFundEditBinding.inflate(inflater, container, false)).root

        val titleView: TextInputLayout = viewBinding.textFundTitle
        val descriptionView: TextInputLayout = viewBinding.textFundText

        fundEditViewModel.selectedFund.observe(
            viewLifecycleOwner,
            Observer { maybeFund ->
                maybeFund.map { fund: Fund ->
                    DataManager.loadFundView(fund.reference).map { fundEditViewModel.showFund(it) }
                }
            })

        fundEditViewModel.titleOfSelectedFund.observe(
            viewLifecycleOwner,
            Observer { maybeTitle: Option<String> ->
                maybeTitle.map { title ->
                    titleView.editText?.setText(title)
                }
            })

        fundEditViewModel.descriptionOfSelectedFund.observe(
            viewLifecycleOwner,
            Observer { maybeDescription: Option<String> ->
                maybeDescription.map { description ->
                    descriptionView.editText?.setText(description)
                }
            })

        val safeArgs: FundEditFragmentArgs by navArgs()
        val selectedFundRef = safeArgs.selectedFund
        val selectedFund = maybeSelectedFund(selectedFundRef)

        fundEditViewModel.selectFund(selectedFund)

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
        fundEditViewModel.clearToSave()
    }

    private fun saveFund(maybeSelectedFund: Option<Fund>) {
        val maybeValidTitle: Option<String> =
            fundEditViewModel.validTitleToSave.value.toOption().flatten()
        val maybeValidDescription: Option<String> =
            fundEditViewModel.validDescriptionToSave.value.toOption().flatten()

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
                modelViewContract.updateFundList()
            }
    }
}
