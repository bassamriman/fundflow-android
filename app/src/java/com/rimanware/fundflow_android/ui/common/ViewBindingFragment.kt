package com.rimanware.fundflow_android.ui.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse

abstract class ViewBindingFragment<ViewBinding>() :
    Fragment() {
    private var _binding: Option<ViewBinding> = None
    val viewBinding: ViewBinding
        get() = _binding.getOrElse {
            throw IllegalStateException("View Binding should exist between onCreateView and onDestroyView")
        }

    protected fun bindView(viewBinding: ViewBinding): ViewBinding {
        _binding = Some(viewBinding)
        return viewBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = None
    }
}