package com.ihdyo.postit.utils

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: suspend () -> Unit = {}
): T {
    val data = withTimeoutOrNull(timeUnit.toMillis(time)) {
        val latch = CompletableDeferred<Unit>()
        val observer = Observer<T> { value ->
            latch.complete(Unit)
        }
        withContext(Dispatchers.Main.immediate) {
            observeForever(observer)
            afterObserve()
            latch.await()
            removeObserver(observer)
            value
        }
    }
    return requireNotNull(data) { "LiveData value was never set." }
}