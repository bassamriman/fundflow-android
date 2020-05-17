package com.rimanware.fundflow_android.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

const val VM_KEY = "modelViewKeyContract"
const val GLOBAL_VM_KEY = "globalModelViewKeyContract"

fun <T> Fragment.viewModelContract(): T = this.viewModelContract<T>(VM_KEY)

fun <T> Fragment.viewModelContract(key: String): T {
    val classArg: ClassArg = arguments?.getSerializable(key) as ClassArg
    val clazz: Class<ViewModel> = classArg.clazz as Class<ViewModel>
    return ViewModelProvider(requireActivity()).get(clazz) as T
}

fun <T> Bundle.registerViewModelContract(key: String, viewModelContract: Class<T>): Bundle {
    return this.apply {
        putSerializable(key, ClassArg(viewModelContract))
    }
}

class ViewModelContractFragmentFactoryImpl<T, F : Fragment>(
    private val contract: Class<T>,
    private val fragmentIdentifierClass: Class<F>,
    private val fragmentConstructor: (Class<T>) -> F
) :
    FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            fragmentIdentifierClass.name -> fragmentConstructor(contract)
            else -> super.instantiate(classLoader, className)
        }
    }
}
