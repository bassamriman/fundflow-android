package com.rimanware.fundflow_android.ui.fund_edit

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.extensions.list.foldable.exists
import com.google.android.material.textfield.TextInputLayout
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.ui.fund_edit.StringRules.validateNotBiggerThan

interface ValidationError {
    val message: String
}

data class StringBiggerThanLimit(val limit: Int) : ValidationError {
    override val message: String = "String bigger then limit of $limit character"
}

object StringIsEmpty : ValidationError {
    override val message: String = "Empty String"
}

object StringRules {
    fun validateNotBiggerThan(limit: Int): (String) -> Validated<ValidationError, String> =
        { s: String -> if (s.length <= limit) Valid(s) else Invalid(StringBiggerThanLimit(limit)) }

    val validateNotEmpty: (String) -> Validated<ValidationError, String> =
        { s: String -> if (s.isNotEmpty()) Valid(s) else Invalid(StringIsEmpty) }
}

object FundTitleAlreadyExist : ValidationError {
    override val message: String = "Fund title already exits"
}

object FundRules {
    fun validateFundTitleIsNotAlreadyTaken(
        newTitle: String,
        oldTitle: String
    ): Validated<ValidationError, String> =
        if (DataManager.loadAllFunds().filterNot { it.name == oldTitle }
                .exists { it.name == newTitle }) Invalid(
            FundTitleAlreadyExist
        ) else Valid(newTitle)
}

object FundEditValidation {
    private val titleCharLimit: Int = 20
    private val descriptionCharLimit: Int = 120

    fun validateFundTitle(
        newTitle: String,
        oldTitle: String
    ): Validated<ValidationError, String> =
        when (val result = validateNotBiggerThan(titleCharLimit)(newTitle)) {
            is Invalid -> result
            is Valid -> FundRules.validateFundTitleIsNotAlreadyTaken(result.a, oldTitle)
        }

    fun setValidateFundTitleError(
        newTitle: String,
        oldTitle: String,
        view: TextInputLayout
    ): Unit = when (val result = validateFundTitle(newTitle, oldTitle)) {
        is Invalid -> {
            view.error = result.e.message
        }
        is Valid -> view.error = null
    }

    fun validateFundDescription(
        newDescription: String
    ): Validated<ValidationError, String> =
        validateNotBiggerThan(descriptionCharLimit)(newDescription)

    fun setValidateFundDescriptionError(
        newDescription: String,
        view: TextInputLayout
    ): Unit = when (val result = validateFundDescription(newDescription)) {
        is Invalid -> {
            view.error = result.e.message
        }
        is Valid -> view.error = null
    }
}
