package matej.lamza.betshops.utils.extensions

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import matej.lamza.betshops.common.State
import kotlin.coroutines.CoroutineContext

fun exceptionHandler(onError: ((Throwable) -> Unit)) =
    CoroutineExceptionHandler { _, exception ->
        Log.e("ViewModel", "Error in ViewModel", exception)
        onError(exception)
    }

fun exceptionHandler(data: MutableLiveData<State>? = null) =
    exceptionHandler { data?.postValue(State.Error(it)) }

fun ViewModel.launch(
    context: CoroutineContext = exceptionHandler(null),
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context, start) {
        block(this)
    }
}

/**
 * Extension function used in ViewModel.
 * @param data - Will hold data regarding state (Loading,Error, Done) will be mutated throughout the function.
 * @param onLoading - default parameter just sets state to Loading
 * @param onDone - default parameter setting state to Done
 * @param onError - default parameter sets state to Error with exception thrown from lambda
 * @param context -
 *
 *
 * Function starts viewmodel scope with provided exception handler and Coroutine Start for immediat execution.
 * Once function starts it will set state to Loading by default and start executing suspending block we passed.
 * Once block is executed onDone is being called, if at any point of execution error happens, exception handler will
 * change state of [data] to Error.
 *
 */
@Suppress("LongParameterList")
fun ViewModel.launchWithState(
    data: MutableLiveData<State>,
    onLoading: (() -> Unit)? = { data.value = State.Loading },
    onDone: (() -> Unit)? = { data.value = State.Done() },
    onError: ((Throwable) -> Unit)? = { data.value = State.Error(it) },
    context: CoroutineContext =
        if (onError != null) exceptionHandler(onError)
        else exceptionHandler(data),
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context, start) {
        onLoading?.invoke()
        block(this)
        onDone?.invoke()
    }
}
