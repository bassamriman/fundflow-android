package com.rimanware.fundflow_android.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import arrow.core.Option
import arrow.core.extensions.option.monad.flatten
import arrow.core.toOption

fun <T1, T2> combineTuple(
    f1: LiveData<Option<T1>>,
    f2: LiveData<Option<T2>>
): LiveData<Pair<Option<T1>, Option<T2>>> =
    MediatorLiveData<Pair<Option<T1>, Option<T2>>>().also { mediator ->
        mediator.value = Pair(f1.value.toOption().flatten(), f2.value.toOption().flatten())

        mediator.addSource(f1) { t1: Option<T1> ->
            mediator.value.toOption().map {
                val (_, t2) = it
                mediator.value = Pair(t1, t2)
            }
        }

        mediator.addSource(f2) { t2: Option<T2> ->
            mediator.value.toOption().map {
                val (t1, _) = it
                mediator.value = Pair(t1, t2)
            }
        }
    }

fun <T1, T2, T3> combineTriple(
    f1: LiveData<Option<T1>>,
    f2: LiveData<Option<T2>>,
    f3: LiveData<Option<T3>>
): LiveData<Triple<Option<T1>, Option<T2>, Option<T3>>> =
    MediatorLiveData<Triple<Option<T1>, Option<T2>, Option<T3>>>().also { mediator ->
        mediator.value = Triple(
            f1.value.toOption().flatten(),
            f2.value.toOption().flatten(),
            f3.value.toOption().flatten()
        )

        mediator.addSource(f1) { t1: Option<T1> ->
            mediator.value.toOption().map {
                val (_, t2, t3) = it
                mediator.value = Triple(t1, t2, t3)
            }
        }

        mediator.addSource(f2) { t2: Option<T2> ->
            mediator.value.toOption().map {
                val (t1, _, t3) = it
                mediator.value = Triple(t1, t2, t3)
            }
        }

        mediator.addSource(f3) { t3: Option<T3> ->
            mediator.value.toOption().map {
                val (t1, t2, _) = it
                mediator.value = Triple(t1, t2, t3)
            }
        }
    }
