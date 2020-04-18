package com.rimanware.fundflow_android.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


const val VM_KEY = "modelViewKeyContract"

fun <T> Fragment.viewModelContract(): T {
    val a = arguments
    val classArg: ClassArg = arguments?.getSerializable(VM_KEY) as ClassArg
    val clazz: Class<ViewModel> = classArg.clazz as Class<ViewModel>
    return ViewModelProvider(requireActivity()).get(clazz) as T
}

fun <T> registerViewModelContract(viewModelContract: Class<T>): Bundle {
    return Bundle().apply {
        putSerializable(VM_KEY, ClassArg(viewModelContract))
    }
}