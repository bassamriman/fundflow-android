package com.rimanware.fundflow_android.ui.fund

import arrow.core.Invalid
import arrow.core.Option
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.extensions.list.foldable.exists
import arrow.core.getOrElse
import arrow.core.toOption
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.ui.common.ValidationError
import fundflow.Fund

object FundTitleAlreadyExist : ValidationError {
    override val message: String = "Fund title already exits"
}

object FundTitleDoesntExist : ValidationError {
    override val message: String = "Fund title doesn't exits"
}

object FundRules {
    fun validateFundTitleIsNotAlreadyTaken(
        newTitle: String,
        oldTitle: Option<String>
    ): Validated<ValidationError, String> =
        if (DataManager.loadAllFunds().filterNot { it.name == oldTitle.getOrElse { "" } }
                .exists { it.name == newTitle }) Invalid(
            FundTitleAlreadyExist
        ) else Valid(newTitle)

    fun validateFundExists(title: String): Validated<ValidationError, Fund> =
        DataManager.loadAllFunds().find { it.name == title }.toOption().map {
            Valid(it)
        }.getOrElse { Invalid(FundTitleDoesntExist) }
}
