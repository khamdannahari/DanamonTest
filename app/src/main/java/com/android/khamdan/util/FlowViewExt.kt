package com.android.khamdan.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

object FlowViewExt {

    fun <T> Flow<T>.safeCollectUnique(
        lifecycleOwner: LifecycleOwner,
        action: suspend (T) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                distinctUntilChanged().collect { value -> action(value) }
            }
        }
    }

    inline fun <reified T> Flow<Event<T?>>.safeCollectEvent(
        lifecycleOwner: LifecycleOwner,
        crossinline action: suspend (T) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { event ->
                    event.getContentIfNotHandled()?.let {
                        action(it)
                    }
                }
            }
        }
    }

}