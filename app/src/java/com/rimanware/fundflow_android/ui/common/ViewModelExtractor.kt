package com.rimanware.fundflow_android.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T> Fragment.viewModelContracts(): Lazy<T> {
    return lazy {
        this.viewModelContract<T>()
    }
}

inline fun <reified T : ViewModel> Fragment.viewModels(): Lazy<T> {
    return lazy {
        ViewModelProvider(requireActivity()).get(T::class.java)
    }
}
