package com.rimanware.fundflow_android.ui.common

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.getOrElse
import arrow.core.toOption
import common.unit.TimeFrequency
import java.math.BigDecimal

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

object TimeFrequencyDoesntExist : ValidationError {
    override val message: String = "Time Frequency title doesn't exit"
}

fun valideTimeFrequencyExist(name: String): Validated<ValidationError, TimeFrequency> =
    TimeFrequency.all.find { it.name == name }.toOption().map {
        Valid(it)
    }.getOrElse { Invalid(TimeFrequencyDoesntExist) }

object AmmountShouldBeBiggerThanZero : ValidationError {
    override val message: String = "Amount should be bigger then zero"
}

object NumberRules {
    fun validateBiggerThanZero(number: BigDecimal): Validated<ValidationError, BigDecimal> =
        if (number > BigDecimal.ZERO) Valid(number) else Invalid(AmmountShouldBeBiggerThanZero)
}
