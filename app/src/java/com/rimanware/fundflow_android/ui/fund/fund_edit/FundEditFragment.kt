package com.rimanware.fundflow_android.ui.fund.fund_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
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
import com.rimanware.fundflow_android.databinding.FragmentFundEditBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModelContracts
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_list.UpdateFundListViewModelContract
import fundflow.Fund

class FundEditFragment : ViewBindingFragment<FragmentFundEditBinding>() {

    private val fundEditViewModel: FundEditViewModel by viewModels()
    private val modelViewContract: UpdateFundListViewModelContract by viewModelContracts()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = bindView(FragmentFundEditBinding.inflate(inflater, container, false)).root

        val titleView: TextInputLayout = viewBinding.textFundTitle
        val descriptionView: TextInputLayout = viewBinding.textFundText

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
                modelViewContract.updateFundList()
            }
    }
}