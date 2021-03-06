package com.rimanware.fundflow_android.ui.fund.fund_edit

import arrow.core.Invalid
import arrow.core.None
import arrow.core.Option
import arrow.core.Valid
import arrow.core.Validated
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.ui.common.StringRules
import com.rimanware.fundflow_android.ui.common.ValidationError
import com.rimanware.fundflow_android.ui.fund.FundRules

object FundEditValidation {
    private val titleCharLimit: Int = 20
    private val descriptionCharLimit: Int = 120

    fun validateFundTitle(
        newTitle: String,
        oldTitle: Option<String>
    ): Validated<ValidationError, String> =
        when (val result = StringRules.validateNotBiggerThan(titleCharLimit)(newTitle)) {
            is Invalid -> result
            is Valid -> FundRules.validateFundTitleIsNotAlreadyTaken(result.a, oldTitle)
        }

    fun handleFundTitleValidation(
        newTitle: String,
        oldTitle: Option<String>,
        view: TextInputLayout,
        fundEditViewModel: FundEditViewModel
    ): Unit = when (val result = validateFundTitle(newTitle, oldTitle)) {
        is Invalid -> {
            view.error = result.e.message
            fundEditViewModel.setTitleToSave(None)
        }
        is Valid -> {
            view.error = null
            fundEditViewModel.setTitleToSave(result.toOption())
        }
    }

    fun validateFundDescription(
        newDescription: String
    ): Validated<ValidationError, String> =
        StringRules.validateNotBiggerThan(descriptionCharLimit)(newDescription)

    fun handleFundDescriptionValidation(
        newDescription: String,
        view: TextInputLayout,
        fundEditViewModel: FundEditViewModel
    ): Unit = when (val result = validateFundDescription(newDescription)) {
        is Invalid -> {
            view.error = result.e.message
            fundEditViewModel.setValidDescriptionToSave(None)
        }
        is Valid -> {
            view.error = null
            fundEditViewModel.setValidDescriptionToSave(result.toOption())
        }
    }
}
