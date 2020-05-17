package com.rimanware.fundflow_android.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rimanware.fundflow_android.ui.core.AppActivity

fun <T> Fragment.viewModelContracts(): Lazy<T> {
    return lazy {
        this.viewModelContract<T>()
    }
}

fun <T> Fragment.viewModelContracts(key: String): Lazy<T> {
    return lazy {
        this.viewModelContract<T>(key)
    }
}

inline fun <reified T : ViewModel> Fragment.viewModels(): Lazy<T> {
    return lazy {
        ViewModelProvider(requireActivity()).get(T::class.java)
    }
}

inline fun <reified T : ViewModel> AppActivity.viewModels(): Lazy<T> {
    return lazy {
        ViewModelProvider(this).get(T::class.java)
    }
}
