package com.rimanware.fundflow_android.ui.fund

import arrow.core.*
import arrow.core.extensions.list.foldable.exists
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.ui.common.ValidationError

object FundTitleAlreadyExist : ValidationError {
    override val message: String = "Fund title already exits"
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
}
