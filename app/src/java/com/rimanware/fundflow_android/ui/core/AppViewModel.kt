package com.rimanware.fundflow_android.ui.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import arrow.core.Option
import arrow.core.toOption
import java.time.LocalDateTime
import kotlinx.coroutines.delay

class AppViewModel : ViewModel(), GlobalDateTimeProviderModelViewContract {

    override val globalDateTime: LiveData<Option<LocalDateTime>> = liveData {
        while (true) {
            emit(LocalDateTime.now().toOption())
            delay(1000)
        }
    }
}

interface GlobalDateTimeProviderModelViewContract {
    val globalDateTime: LiveData<Option<LocalDateTime>>
}
