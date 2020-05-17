package com.rimanware.fundflow_android.ui.common

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated

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
