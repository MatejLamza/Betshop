package matej.lamza.betshops.utils.extensions

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import matej.lamza.betshops.common.State
import matej.lamza.betshops.common.base.View
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

fun LiveData<State>.observeState(
    owner: LifecycleOwner,
    view: View? = null,
    onError: ((error: Throwable?) -> Unit)? = null,
    onLoading: (() -> Unit)? = null,
    onDone: ((hasData: Boolean?) -> Unit)? = null
) {
    observe(owner) {
        when (it) {
            is State.Loading -> onLoading?.invoke() ?: view?.showLoading()
            is State.Done -> {
                view?.dismissLoading()
                onDone?.invoke(it.hasData)
            }
            is State.Error -> {
                onError?.invoke(it.throwable)
                    ?: it.throwable?.let { error -> view?.showError(error) }
            }
        }
    }
}

fun <T> Flow<T>.asLiveDataWithState(
    data: MutableLiveData<State>? = null,
    onLoading: (() -> Unit)? =
        if (data != null) ({ data.value = State.Loading })
        else null,
    onError: ((Throwable) -> Unit)? =
        if (data != null) ({ data.value = State.Error(it) })
        else null,
    onDone: (() -> Unit)? =
        if (data != null) ({ data.value = State.Done() })
        else null,
    context: CoroutineContext =
        if (onError != null) exceptionHandler(onError = onError)
        else exceptionHandler(data)
): LiveData<T> = onStart {
    Log.d("bbb", "asLiveDataWithState: tu sam loading")
    onLoading?.invoke()
}
    .catch { onError?.invoke(it) }
    .onEach {
        Log.d("bbb", "asLiveDataWithState: gotovo")
        onDone?.invoke()
    }
    .asLiveData(context)
