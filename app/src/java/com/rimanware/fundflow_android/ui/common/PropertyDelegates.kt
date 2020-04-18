package com.rimanware.fundflow_android.ui.common

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import kotlin.reflect.KProperty

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}


class OptionCheckerDelegate<T>(private var option: Option<T>, private val errorMessage: String = "Value is not set") {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = option.getOrElse {
        throw IllegalStateException(errorMessage)
    }
}



